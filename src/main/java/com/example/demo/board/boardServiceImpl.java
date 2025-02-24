package com.example.demo.board;

import com.example.demo.common.commonServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.ArrayList;
import java.util.List;

@Service
public class boardServiceImpl implements boardService {
    @Autowired
    boardMapper boardMapper;

    @Autowired
    commonServiceImpl commonServiceImpl;
    @Override
    public boardPagingDTO boardSelectList(Integer currentPage) throws Exception {
        List<boardDTO> boardList = new ArrayList<>();
        //boardPagingDTO boardPagingDTO;

        boardList = boardMapper.boardSelectList(currentPage  == 1 ? 0 :  currentPage * 5 / 2);

        boardPagingDTO boardPagingDTO = new boardPagingDTO(boardList.get(0).getTotalCount() , currentPage , 5,5,boardList);

        return boardPagingDTO;
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


        return null;
    }

    @Override
    public boardDTO boardUdate(boardDTO boardDto, MultipartFile[] files) throws Exception {
        return null;
    }
}
