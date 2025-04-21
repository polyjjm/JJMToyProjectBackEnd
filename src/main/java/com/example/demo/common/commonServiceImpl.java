package com.example.demo.common;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.example.demo.board.boardDTO;
import com.example.demo.board.boardMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import java.net.URLDecoder;
import java.util.*;

@Service
@RequiredArgsConstructor
public class commonServiceImpl implements commonService {

    private final AmazonS3 amazonS3;
    private final AmazonS3Client amazonS3Client;

    @Autowired
    boardMapper boardMapper;

    @Value("${cloud.aws.s3.bucket}")
    private String bucket;
    @Override
    public List<String> ckEditorUpload(MultipartFile[] upload ) throws Exception {

        String newFileName = null;

        // 확장자 구하기
        List<String> urlList = new ArrayList<String>();
        for (MultipartFile file : upload) {
            ObjectMetadata metadata = new ObjectMetadata();
            metadata.setContentLength(file.getSize());
            metadata.setContentType(file.getContentType());

            String extension = StringUtils.getFilenameExtension(file.getOriginalFilename());
            UUID uuid = UUID.randomUUID();
            newFileName = uuid + "@" + file.getOriginalFilename();

            amazonS3.putObject(bucket, newFileName, file.getInputStream(), metadata);


            urlList.add(amazonS3.getUrl(bucket , newFileName).toString());
        }

        return urlList;

    }
    public String deleteFile(String filename) {

        String result = "success";
        // 문자열에서 "static/profile/" 다음의 부분을 추출

        try {
            String url = URLDecoder.decode(filename);
            boolean isObjectExist = amazonS3Client.doesObjectExist(bucket, url);
            if (isObjectExist) {
                amazonS3Client.deleteObject(bucket, url);
            } else {
                result = "file not found";
            }
        } catch (Exception e) {

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
