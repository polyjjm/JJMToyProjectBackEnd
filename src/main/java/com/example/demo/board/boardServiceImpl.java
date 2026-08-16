package com.example.demo.board;

import com.example.demo.common.CustomException;
import com.example.demo.common.ErrorCode;
import com.example.demo.common.commonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class boardServiceImpl implements boardService {
    @Autowired
    boardMapper boardMapper;

    @Autowired
    commonServiceImpl commonServiceImpl;

    // Reads the logged-in user's email out of the JWT that JwtTokenFilter already validated
    // and placed on the SecurityContext (subject claim) - same pattern as
    // boardCommentServiceImpl.currentUserEmail(). Callers here are only reachable once
    // securityConfig requires authentication for board write endpoints, so auth is always
    // non-null at this point, but we still guard defensively.
    private String currentUserEmail() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || auth.getName() == null || "anonymousUser".equals(auth.getName())) {
            throw new CustomException(ErrorCode.LOGIN_REQUIRED, ErrorCode.LOGIN_REQUIRED.getErrorCode());
        }
        return auth.getName();
    }

    // Denies update/delete unless the requester is the post's original author. board_userName
    // on a legacy/anonymous row can be null - that must also be denied, not treated as "no
    // owner to check against".
    private void assertOwner(Integer board_no) {
        String owner = boardMapper.selectAuthor(board_no);
        String currentUser = currentUserEmail();
        if (owner == null || !owner.equals(currentUser)) {
            throw new CustomException(ErrorCode.BOARD_FORBIDDEN, ErrorCode.BOARD_FORBIDDEN.getErrorCode());
        }
    }
    @Override
    public Map<String,Object> boardSelectList(Integer scrollIndex) throws Exception {
        List<boardDTO> boardList = new ArrayList<>();

        boardList = boardMapper.boardSelectList(scrollIndex);
        Integer totalCount = boardMapper.boardSearchCount();
        Map returnMap = new HashMap();
        returnMap.put("searchData" , boardList);
        returnMap.put("totalCount" , totalCount);

        return returnMap;
    }

    @Override
    public boardDTO boardInsert(boardDTO boardDto , MultipartFile[] files) throws Exception {

        List<String> urlList = new ArrayList<String>();
        StringBuilder fileName = new StringBuilder();
        // 파일 업로드
        List<fileSrcNameDTO> imgList = new ArrayList<fileSrcNameDTO>();
        List<fileSrcNameDTO> imgLegacyList = new ArrayList<fileSrcNameDTO>();
        List<fileSrcNameDTO> finalList = new ArrayList<fileSrcNameDTO>();

        imgList = boardDto.getBoardImgList();
        imgLegacyList = boardDto.getBoardImgLegacyList();

        for(int i = 0 ; i < imgLegacyList.size(); i++ ){

            for(int j = 0; j < imgList.size() ; j++){

                if(imgList.get(j).getValue().contains(imgLegacyList.get(i).getValue())){
                    imgList.get(j).setValue(
                            imgList.get(j).getValue().
                                    replace("src=\"" , "").
                                    replace("jpg\"" , "jpg").
                                    replace("png\"" , "png").
                                    replace("gif\"" , "gif").
                                    replace("bmp\"" , "bmp").
                                    replace("tiff\"" , "tiff").
                                    replace("raw\"" , "raw")

                    );
                    finalList.add(imgList.get(j));

                }

            }


        }

        String value = "";

        for(int i =  0 ; i<imgLegacyList.size();i++){
            value = imgLegacyList.get(i).getValue();
            for(int j = 0 ; j < finalList.size(); j++){
                if(!value.contains(finalList.get(j).getValue())){
                    // toObjectKey strips both the src="..." wrapper and the public URL prefix -
                    // see commonServiceImpl for why this used to be a hardcoded old-AWS-bucket
                    // URL replace() that silently stopped matching once URLs moved to MinIO.
                    commonServiceImpl.deleteFile(commonServiceImpl.toObjectKey(imgLegacyList.get(i).getValue()));
                }
            }


        }


        boardDto.setBoard_writer("주종민");
        // Never trust a client-supplied author identity - derive it from the validated JWT,
        // same as boardCommentServiceImpl does for comment_userName. Without this, anyone could
        // set board_userName to an arbitrary value (including someone else's email) and later
        // pass the ownership check in assertOwner().
        boardDto.setBoard_userName(currentUserEmail());

        String imgListText = "";
        // 이미지 리스트 텍스트로 만들기  "," 추가
        for(int i = 0 ; i < imgList.size(); i++){
            imgList.get(i).setValue(
                    imgList.get(i).getValue().
                            replace("src=\"" , "").
                            replace("jpg\"" , "jpg").
                            replace("png\"" , "png").
                            replace("gif\"" , "gif").
                            replace("bmp\"" , "bmp").
                            replace("tiff\"" , "tiff").
                            replace("raw\"" , "raw")

            );
            imgListText  += imgList.get(i).getValue() + ',';
        }
//        if(imgListText.endsWith(",")){
//            imgListText = imgListText.substring(0,imgListText.length() -1);
//        }
        boardDto.setBoardImgListText(imgListText);

        boardMapper.boardInsert(boardDto);

        return null;
    }

    @Override
    public boardDTO boardUdate(boardDTO boardDto, MultipartFile[] files) throws Exception {
        assertOwner(boardDto.getBoard_no());

        List<fileSrcNameDTO> imgList = boardDto.getBoardImgList();
        List<fileSrcNameDTO> imgLegacyList = boardDto.getBoardImgLegacyList();
        List<fileSrcNameDTO> finalList = new ArrayList<>();

        for (fileSrcNameDTO legacy : imgLegacyList) {
            for (fileSrcNameDTO img : imgList) {
                if (img.getValue().contains(legacy.getValue())) {
                    img.setValue(img.getValue()
                            .replace("src=\"", "")
                            .replace("jpg\"", "jpg")
                            .replace("png\"", "png")
                            .replace("gif\"", "gif")
                            .replace("bmp\"", "bmp")
                            .replace("tiff\"", "tiff")
                            .replace("raw\"", "raw"));
                    finalList.add(img);
                }
            }
        }

        for (fileSrcNameDTO legacy : imgLegacyList) {
            boolean exists = finalList.stream()
                    .anyMatch(finalImg -> legacy.getValue().contains(finalImg.getValue()));
            if (!exists) {
                commonServiceImpl.deleteFile(commonServiceImpl.toObjectKey(legacy.getValue()));
            }
        }

        boardDto.setBoard_writer("주종민");

        StringBuilder imgListText = new StringBuilder();
        for (fileSrcNameDTO img : imgList) {
            img.setValue(img.getValue()
                    .replace("src=\"", "")
                    .replace("jpg\"", "jpg")
                    .replace("png\"", "png")
                    .replace("gif\"", "gif")
                    .replace("bmp\"", "bmp")
                    .replace("tiff\"", "tiff")
                    .replace("raw\"", "raw"));
            imgListText.append(img.getValue()).append(',');
        }

        boardDto.setBoardImgListText(imgListText.toString());

        boardMapper.boardUpdate(boardDto);

        return null;
    }

    @Override
    public void boardView(String board_no) throws Exception {

        boardMapper.boardView(board_no);
    }

    @Override
    public void boardDelete(Integer board_no) throws Exception {
        assertOwner(board_no);
        boardMapper.boardDelete(board_no);
    }

    @Override
    public List<boardCategoryCountDTO> boardCategoryTree() throws Exception {
        return boardMapper.boardCategoryTree();
    }
}
