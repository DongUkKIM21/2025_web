package com.example.project.util;

public class BoardPage {
    public static String pagingStr(int totalCount, int pageSize, int blockPage,
            int pageNum, String reqUrl, String searchField, String searchWord, String category) {
        
        StringBuilder pagingStr = new StringBuilder();
        
        int totalPages = (int) (Math.ceil(((double) totalCount / pageSize)));
        
        String searchParams = "";
        if (searchWord != null && !searchWord.equals("")) {
            searchParams = "&searchField=" + searchField + "&searchWord=" + searchWord;
        }
        
        int pageTemp = (((pageNum - 1) / blockPage) * blockPage) + 1;
        if (pageTemp != 1) {
            pagingStr.append("<a href='").append(reqUrl).append("?pageNum=1").append(searchParams).append(category != null ? "&category=" + category : "").append("'>[첫 페이지]</a>");
            pagingStr.append("&nbsp;");
            pagingStr.append("<a href='").append(reqUrl).append("?pageNum=").append(pageTemp - 1).append(searchParams).append(category != null ? "&category=" + category : "").append("'>[이전 블록]</a>");
        }
        
        int blockCount = 1;
        while (blockCount <= blockPage && pageTemp <= totalPages) {
            if (pageTemp == pageNum) {
                pagingStr.append("&nbsp;").append(pageTemp).append("&nbsp;");
            } else {
                pagingStr.append("&nbsp;<a href='").append(reqUrl).append("?pageNum=").append(pageTemp).append(searchParams).append(category != null ? "&category=" + category : "").append("'>").append(pageTemp).append("</a>&nbsp;");
            }
            pageTemp++;
            blockCount++;
        }
        
        if (pageTemp <= totalPages) {
            pagingStr.append("<a href='").append(reqUrl).append("?pageNum=").append(pageTemp).append(searchParams).append(category != null ? "&category=" + category : "").append("'>[다음 블록]</a>");
            pagingStr.append("&nbsp;");
            pagingStr.append("<a href='").append(reqUrl).append("?pageNum=").append(totalPages).append(searchParams).append(category != null ? "&category=" + category : "").append("'>[마지막 페이지]</a>");
        }
        
        return pagingStr.toString();
    }
} 