package com.example.demo.common;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.demo.board.boardDTO;
import com.example.demo.board.boardMapper;
import lombok.RequiredArgsConstructor;
import net.coobird.thumbnailator.Thumbnails;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class commonServiceImpl implements commonService {

    // Single S3 client for everything (upload, delete, existence checks) - see AWSS3Config for
    // why there used to be two different beans here and only one of them actually knew about
    // MinIO.
    private final AmazonS3 amazonS3;

    @Autowired
    boardMapper boardMapper;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;

    // Public, browser-reachable base URL for the bucket (reverse-proxied MinIO) - see
    // application.properties. Image URLs handed to the frontend are built from this rather than
    // AmazonS3.getUrl(), which would bake in the internal endpoint (cloud.aws.s3.endpoint) that
    // real browsers can't reach.
    @Value("${cloud.aws.s3.public-url}")
    private String publicUrl;

    // Thumbnail naming convention: same object key as the original, under a "thumbs/" prefix -
    // e.g. original "<uuid>@photo.jpg" -> thumbnail "thumbs/<uuid>@photo.jpg". This is
    // deliberately a one-directional, string-derivable convention (thumbnail key = "thumbs/" +
    // original key) rather than a second URL threaded through board_imgList/the upload response,
    // so the frontend can compute a thumbnail URL from an original URL with a plain string
    // transform (see board.tsx's toThumbnailUrl) without any DB/response schema changes.
    private static final String THUMB_PREFIX = "thumbs/";
    // Long-edge target, in px - sized for board.tsx's card image (16:9 box inside a
    // minmax(260px, 1fr) grid column, so realistically never wider than ~450px even on a wide
    // desktop), with some headroom for retina displays.
    private static final int THUMB_MAX_DIMENSION = 480;

    @Override
    public List<String> ckEditorUpload(MultipartFile[] upload) throws Exception {
        List<String> urlList = new ArrayList<>();

        for (MultipartFile file : upload) {
            // Buffered into memory (board images go through spring.servlet.multipart.max-file-size
            // = 50MB, so this is bounded) because MultipartFile's InputStream can only be
            // consumed once, and both the original upload and the thumbnail resize need to read
            // the image data independently.
            byte[] bytes = file.getBytes();
            String newFileName = UUID.randomUUID() + "@" + file.getOriginalFilename();

            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(bytes.length);
            metadata.setContentType(file.getContentType());
            amazonS3.putObject(bucket, newFileName, new ByteArrayInputStream(bytes), metadata);

            uploadThumbnail(bytes, file.getContentType(), newFileName);

            urlList.add(buildPublicUrl(newFileName));
        }

        return urlList;
    }

    // Best-effort: if a file isn't a decodable image, or resizing otherwise fails, the original
    // upload above has already succeeded - we just skip the thumbnail rather than failing the
    // whole request. The frontend falls back to the original URL when a thumbnail 404s anyway
    // (see board.tsx), so this degrades gracefully either way.
    private void uploadThumbnail(byte[] originalBytes, String contentType, String key) {
        try {
            ByteArrayOutputStream thumbOut = new ByteArrayOutputStream();
            Thumbnails.of(new ByteArrayInputStream(originalBytes))
                    .size(THUMB_MAX_DIMENSION, THUMB_MAX_DIMENSION)
                    .keepAspectRatio(true)
                    .outputQuality(0.85)
                    .toOutputStream(thumbOut);

            byte[] thumbBytes = thumbOut.toByteArray();
            ObjectMetadata thumbMetadata = new ObjectMetadata();
            thumbMetadata.setContentLength(thumbBytes.length);
            thumbMetadata.setContentType(contentType);
            amazonS3.putObject(bucket, THUMB_PREFIX + key, new ByteArrayInputStream(thumbBytes), thumbMetadata);
        } catch (IOException | RuntimeException e) {
            // Swallowed deliberately - see method comment.
        }
    }

    private String buildPublicUrl(String key) {
        return publicUrl + "/" + bucket + "/" + encodeKeyForUrl(key);
    }

    // URLEncoder is form-encoding (spaces -> '+', not '%20') and encodes '/' along with
    // everything else - our keys only ever contain '/' for the thumbs/ prefix we add ourselves,
    // so each path segment is encoded independently and rejoined to keep the path structure.
    private String encodeKeyForUrl(String key) {
        return Arrays.stream(key.split("/"))
                .map(segment -> URLEncoder.encode(segment, StandardCharsets.UTF_8).replace("+", "%20"))
                .collect(Collectors.joining("/"));
    }

    // Recovers the raw S3 object key from a value that may still carry the src="..." HTML
    // wrapper and/or the full public URL prefix - board content embeds images as raw <img
    // src="..."> tags, and depending on whether an entry came from a fresh upload in this
    // editing session or got re-scraped out of existing content on boardUpdate.tsx's mount, the
    // exact wrapping differs slightly. This strips whichever of those are actually present.
    //
    // Previously this stripping was a hardcoded "https://jjmserverbucket.s3.ap-northeast-2.amazonaws.com/"
    // replace() in boardServiceImpl, which silently stopped matching anything once URLs moved to
    // MinIO's public-url scheme - meaning image cleanup on delete/replace had been quietly
    // no-op'ing. Centralized here since this is the one place that knows both the current
    // publicUrl and bucket.
    public String toObjectKey(String rawValue) {
        String value = rawValue;
        if (value.startsWith("src=\"")) {
            value = value.substring(5);
        }
        if (value.endsWith("\"")) {
            value = value.substring(0, value.length() - 1);
        }
        String prefix = publicUrl + "/" + bucket + "/";
        if (value.startsWith(prefix)) {
            value = value.substring(prefix.length());
        }
        try {
            return URLDecoder.decode(value, StandardCharsets.UTF_8);
        } catch (Exception e) {
            return value;
        }
    }

    // Expects an already-resolved bucket key (see toObjectKey) - deletes the original and, best
    // effort, its thumbs/ counterpart (which may not exist, e.g. thumbnail generation failed at
    // upload time, or this is a pre-thumbnail-era legacy image - either is fine).
    public String deleteFile(String key) {
        String result = "success";
        try {
            if (amazonS3.doesObjectExist(bucket, key)) {
                amazonS3.deleteObject(bucket, key);
            } else {
                result = "file not found";
            }
            String thumbKey = THUMB_PREFIX + key;
            if (amazonS3.doesObjectExist(bucket, thumbKey)) {
                amazonS3.deleteObject(bucket, thumbKey);
            }
        } catch (Exception e) {
            result = "error";
        }

        return result;
    }

    @Override
    public Map<String,Object> search(searchDTO searchDto) throws Exception {

        Map returnMap = new HashMap();


        // 검색word ,  검색 타입 있으면 ,  검색 table
        String tableName = searchDto.getSearchTableName();
        if(searchDto.getSearchWord() == null){

        }
        if(searchDto.getSearchTableName() == null){

        }


        if(tableName.equals("board")){
            List<boardDTO> boardDto = new ArrayList<>();
            boardDto = boardMapper.boardSearch(searchDto);
            Integer totalCount = boardMapper.boardSearchCount();
            returnMap.put("searchData" , boardDto);
            returnMap.put("totalCount" , totalCount);
        }
        return returnMap;
    }

    private static String extractString(String input, String marker) {
        int startIndex = input.indexOf(marker);
        if (startIndex != -1) {
            // marker 다음의 부분을 추출
            return input.substring(startIndex + marker.length());
        } else {
            // marker가 없으면 빈 문자열 반환
            return "";
        }
    }

}
