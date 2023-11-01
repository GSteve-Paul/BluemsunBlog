package com.bluemsun.controller;

import com.bluemsun.entity.JsonResponse;
import com.bluemsun.interceptor.BanChecker;
import com.bluemsun.interceptor.TokenChecker;
import com.bluemsun.util.UriUtil;
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
    @TokenChecker({"user","admin"})
    @BanChecker
    public JsonResponse upload(@RequestParam MultipartFile[] files, HttpServletRequest req) {
        JsonResponse jsonResponse = new JsonResponse();
        int exceptionCnt = 0;
        List<String> filesNameInServer = new ArrayList<>();
        for (MultipartFile file : files) {
            String name = file.getOriginalFilename();
            System.out.println(name);
            Long size = file.getSize();
            System.out.println(size);

            String realPath = uploadPath;
            System.out.println(realPath);

            File newFile = new File(realPath);
            if (!newFile.exists()) {
                newFile.mkdirs();
            }

            String newName = UUID.randomUUID() + name;
            try {
                file.transferTo(new File(newFile, newName));
                String fileServerPath = UriUtil.getServerUri(newName);
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
