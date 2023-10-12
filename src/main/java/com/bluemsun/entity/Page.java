package com.bluemsun.entity;

import java.util.List;

public class Page<T>
{
    /**
     * 用于存放数据库中的数据结果集,使用泛型，增强通用性
     */
    public List<T> list;
    /**
     * 当前是第几页
     */
    private int currentPage;
    /**
     * 一页数据量
     */
    private int pageSize;
    /**
     * 数据库一共数据量
     */
    private int totalRecord;
    /**
     * 总共页数
     */
    private int totalPage;
    /**
     * 数据记录起始位置
     */
    private int startIndex;

    public Page() {}

    public Page(int currentPage, int pageSize, int totalRecord) {
        this.currentPage = currentPage;
        this.pageSize = pageSize;
        this.totalRecord = totalRecord;

        init();
    }

    public int getCurrentPage() {
        return currentPage;
    }

    public int getPageSize() {
        return pageSize;
    }

    public int getTotalRecord() {
        return totalRecord;
    }

    public int getTotalPage() {
        return totalPage;
    }

    public int getStartIndex() {
        return startIndex;
    }

    public void init() {
        // 总页数 = [总记录数/页面大小]，如果不是整除需要+1
        if (totalRecord % pageSize == 0) {
            this.totalPage = totalRecord / pageSize;
        } else {
            this.totalPage = totalRecord / pageSize + 1;
        }
        //计算起始页号，（当前页号-1）*页面大小
        this.startIndex = currentPage * pageSize - pageSize;
    }

}
