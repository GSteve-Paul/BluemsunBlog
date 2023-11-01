package com.bluemsun.dao;

import com.bluemsun.entity.Column;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

@Mapper
public interface ColumnDao
{
    int getAmount();

    List<Column> getPage(int start, int len);

    default List<Column> getAllColumns() {
        int amount = getAmount();
        return getPage(0, amount);
    }

    Long insertColumn(Column column);

    Column getColumn(Long columnId);

    int updateColumn(Column column);

    Integer getBlogAmount(Long columnId);

    List<Long> getBlogsInColumnPage(Long columnId, Integer start, Integer len);
}
