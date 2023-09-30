package com.bluemsun.controller;

import com.bluemsun.entity.JsonResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/file")
public class FileController
{
    @Value("${file.upload.url}")
    private String uploadPath;

    @PostMapping("/upload")
    public JsonResponse upload(@RequestParam MultipartFile[] files, HttpServletRequest req) {
        JsonResponse jsonResponse = new JsonResponse();
        int exceptionCnt = 0;
        List<String> filesNameInServer = new ArrayList<>();
        for (MultipartFile file : files) {
            String name = file.getOriginalFilename();
            System.out.println(name);
            String suffix = name.substring(name.lastIndexOf('.'));
            System.out.println(suffix);
            Long size = file.getSize();
            System.out.println(size);

            String realPath = uploadPath;
            System.out.println(realPath);

            File newFile = new File(realPath);
            if (!newFile.exists()) {
                newFile.mkdirs();
            }

            String newName = UUID.randomUUID() + suffix;
            try {
                file.transferTo(new File(newFile, newName));
                String fileServerPath = req.getScheme() + "://" +
                        req.getServerName() + ":" + "8081" +
                        "/static/" + newName;
                filesNameInServer.add(fileServerPath);
            } catch (IOException e) {
                exceptionCnt++;
                filesNameInServer.add(null);
            }

        }
        if (exceptionCnt == 0) {
            jsonResponse.setCode(2000);
            jsonResponse.setMessage("Success");
            jsonResponse.setData(filesNameInServer);
        } else {
            jsonResponse.setCode(2001);
            jsonResponse.setMessage(exceptionCnt + "exception(s) occur(s)");
            jsonResponse.setData(filesNameInServer);
        }
        return jsonResponse;
    }
}