package com.bluemsun.service;

import com.bluemsun.dao.FileDao;
import com.bluemsun.entity.Ffile;
import com.bluemsun.util.UriUtil;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.io.File;
import java.util.List;

@Service
public class FileService
{
    @Resource
    FileDao fileDao;

    public boolean isInBlog(Long blogId, String fileName) {
        Ffile ffile = fileDao.isInBlog(blogId,fileName);
        return ffile != null;
    }

    public void addFiles(List<String> fileName, Long blogId) {
        fileDao.addFile(fileName,blogId);
    }

    public void removeFile(List<String> fileName, Long blogId) {
        //fileDao.removeFile(fileName,blogId);
        /*
        for(String name:fileName) {
            File file = new File(UriUtil.getLocalUri(name));
            file.delete();
        }
         */
    }

    public List<Ffile> getFiles(Long blogId) {
        return fileDao.getFilesOfBlog(blogId);
    }
}
