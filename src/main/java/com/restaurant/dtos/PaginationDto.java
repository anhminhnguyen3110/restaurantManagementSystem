package com.restaurant.dtos;

public class PaginationDto {
    private int page    = 0;
    private int size    = 20;
    private String sortBy  = "id";
    private String sortDir = "desc";

    public int getPage() {
        return page;
    }
    public void setPage(int page) {
        this.page = Math.max(page, 0);
    }
    public int getSize() {
        return size;
    }
    public void setSize(int size) {
        this.size = size > 0 ? size : 20;
    }
    public String getSortBy() {
        return sortBy;
    }
    public void setSortBy(String sortBy) {
        this.sortBy = sortBy;
    }
    public String getSortDir() {
        return sortDir;
    }
    public void setSortDir(String sortDir) {
        this.sortDir = sortDir;
    }
}
