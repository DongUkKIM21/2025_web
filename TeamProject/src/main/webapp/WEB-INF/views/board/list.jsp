<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
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
            <option value="title" <c:if test="${map.searchField eq 'title'}">selected</c:if>>제목</option>
            <option value="content" <c:if test="${map.searchField eq 'content'}">selected</c:if>>내용</option>
            <option value="id" <c:if test="${map.searchField eq 'id'}">selected</c:if>>작성자</option>
        </select>
        <input type="text" name="searchWord" value="${map.searchWord}" />
        <input type="submit" value="검색하기" />
    </form>

    <p style="text-align:right;">
        <c:if test="${not empty sessionScope.userId}">
            ${sessionScope.userNickname}님 환영합니다.
            <a href="<c:url value='/board/write.do' />">[글쓰기]</a>
            <a href="<c:url value='/member/edit.do?id=${sessionScope.userId}' />">[정보 수정]</a>
            <a href="<c:url value='/member/logout.do' />">[로그아웃]</a>
            <c:if test="${sessionScope.userAdmin eq 1}">
                | <a href="<c:url value='/member/list.do' />">[회원 관리]</a>
            </c:if>
        </c:if>
    </p>

    <table border="1" width="90%">
        <tr>
            <th>번호</th>
            <th>제목</th>
            <th>작성자</th>
            <th>작성일</th>
            <th>조회수</th>
        </tr>
        <c:choose>
            <c:when test="${empty boardList}">
                <tr>
                    <td colspan="5" align="center">등록된 게시물이 없습니다.</td>
                </tr>
            </c:when>
            <c:otherwise>
                <c:forEach items="${boardList}" var="post" varStatus="status">
                    <tr>
                        <td>${map.totalCount - (map.pageSize * (map.pageNum - 1)) - status.index}</td>
                        <td>
                            <a href="view.do?num=${post.num}">${post.title}</a>
                        </td>
                        <td>${post.nickname}</td>
                        <td>${post.postdate}</td>
                        <td>${post.visitcount}</td>
                    </tr>
                </c:forEach>
            </c:otherwise>
        </c:choose>
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