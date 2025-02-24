package com.example.demo.common;

import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.List;

public interface commonService {


    public List<String> ckEditorUpload(MultipartFile[] upload) throws Exception;

    public String deleteFile(String fileName) throws Exception;
}
