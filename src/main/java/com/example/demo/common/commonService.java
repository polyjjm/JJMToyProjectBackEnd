package com.example.demo.common;

import org.springframework.web.multipart.MultipartFile;
import java.util.List;
import java.util.Map;

public interface commonService {


    public List<String> ckEditorUpload(MultipartFile[] upload) throws Exception;

    public String deleteFile(String fileName) throws Exception;
    public Map<String,Object> search(searchDTO searchDTO) throws Exception;
}
