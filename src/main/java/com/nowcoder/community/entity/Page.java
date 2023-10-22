package com.nowcoder.community.entity;

/*
封装分页相关的信息
 */
public class Page {
    // current page
    private int current = 1;

    // records per page
    private int limit = 10;

    // count page size
    private int rows;

    // 查询路径（用于复用分页链接）
    private String path;

    public int getCurrent() {
        return current;
    }

    public void setCurrent(int current) {
        if(current >= 1) {
            this.current = current;
        }
    }

    public int getLimit() {
        return limit;
    }

    public void setLimit(int limit) {
        if (limit >= 1 && limit <= 100) {
            this.limit = limit;
        }
    }

    public int getRows() {
        return rows;
    }

    public void setRows(int rows) {
        if (rows >= 0) {
            this.rows = rows;
        }
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
    // 获取当前页的起始行
    public int getOffset() {
        return (current - 1) * limit;
    }

    public int getTotal(){
        // rows / limit
        return (rows - 1) / limit + 1;
    }
    /*
    获取起始页码
     */
    public int getFrom() {
        int from = current - 2;
        return from < 1 ? 1 : from;
    }

    /**
     * 获取结束页吗
     * @return
     */

    public int getTo() {
        int to = current + 2;
        int total = getTotal();
        return to > total ? total : to;
    }
}
