package com.bluemsun.dao;

import com.bluemsun.entity.Blog;
import com.bluemsun.entity.Column;

import java.util.List;

public interface ColumnDao
{
    int getAmount();
    List<Column> getPage(int start,int len);
    default List<Column> getAllColumns() {
    int amount = getAmount();
    return getPage(0,amount - 1);
}

    Long insertColumn(Column column);
    Column getColumn(Long columnId);
    int updateColumn(Column column);
}
