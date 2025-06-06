<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.example.project.dto.BoardDTO" %>
<%@ page import="java.util.Map" %>
<%
    List<BoardDTO> boardList = (List<BoardDTO>) request.getAttribute("boardList");
    Map<String, Object> map = (Map<String, Object>) request.getAttribute("map");
    String searchField = map.get("searchField") != null ? (String)map.get("searchField") : "";
    String searchWord = map.get("searchWord") != null ? (String)map.get("searchWord") : "";
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판 목록</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <h2>게시판 목록</h2>

    <form method="get">
        <select name="searchField">
            <option value="title" <% if("title".equals(searchField)) out.print("selected"); %>>제목</option>
            <option value="content" <% if("content".equals(searchField)) out.print("selected"); %>>내용</option>
            <option value="id" <% if("id".equals(searchField)) out.print("selected"); %>>작성자</option>
        </select>
        <input type="text" name="searchWord" value="${map.searchWord}" />
        <input type="submit" value="검색하기" />
    </form>

    <p style="text-align:right;">
        <% if (session.getAttribute("userId") != null) { %>
            <a href="write.do">[글쓰기]</a>
        <% } %>
    </p>

    <table border="1" width="90%">
        <tr>
            <th>번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
            <th>조회수</th>
        </tr>
        <%
        if (boardList == null || boardList.isEmpty()) {
        %>
            <tr>
                <td colspan="5" align="center">등록된 게시물이 없습니다.</td>
            </tr>
        <%
        } else {
            int loopCounter = 0;
            for (BoardDTO post : boardList) {
                // Set post as an attribute to be accessible by EL
                pageContext.setAttribute("post", post);
                int totalCount = (Integer)map.get("totalCount");
                int pageNum = (Integer)map.get("pageNum");
                int pageSize = 10; // Controller's page size
                int virtualNum = totalCount - (((pageNum - 1) * pageSize) + loopCounter);
        %>
            <tr>
                <td><%= virtualNum %></td>
                <td>
                    <a href="view.do?num=${post.num}">${post.title}</a>
                </td>
                <td>${post.id}</td>
                <td>${post.postdate}</td>
                <td>${post.visitcount}</td>
            </tr>
        <%
                loopCounter++;
            }
        }
        %>
    </table>

    <table width="90%">
        <tr align="center">
            <td>
                ${map.pagingImg}
            </td>
        </tr>
    </table>
</body>
</html> 