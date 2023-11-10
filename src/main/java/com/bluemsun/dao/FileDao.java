package com.bluemsun.dao;

import com.bluemsun.entity.Ffile;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface FileDao
{
    int removeFile(Long blogId);

    int addFile(List<String> fileName, Long blogId);

    Ffile isInBlog(Long blogId, String fileName);

    List<Ffile> getFilesOfBlog(Long blogId);
}
