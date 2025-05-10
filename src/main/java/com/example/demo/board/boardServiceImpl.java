package com.example.demo.board;

import com.example.demo.common.commonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
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
                    commonServiceImpl.deleteFile(imgLegacyList.get(i).getValue().replace("https://jjmserverbucket.s3.ap-northeast-2.amazonaws.com/",""));
                }
            }


        }


        boardDto.setBoard_writer("주종민");


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
                commonServiceImpl.deleteFile(legacy.getValue().replace("https://jjmserverbucket.s3.ap-northeast-2.amazonaws.com/", ""));
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
        boardMapper.boardDelete(board_no);
    }
}
