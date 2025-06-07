<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ page import="java.util.List" %>
<%@ page import="com.example.project.dto.BoardDO" %>
<%@ page import="java.util.Map" %>
<%
    List<BoardDO> boardList = (List<BoardDO>) request.getAttribute("boardList");
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
<style>
    .board-container { display: flex; }
    .category-menu { width: 150px; margin-right: 20px; }
    .category-menu ul { list-style-type: none; padding: 0; }
    .category-menu li a { display: block; padding: 10px; border-bottom: 1px solid #ddd; text-decoration: none; color: #333; }
    .category-menu li a.active { font-weight: bold; color: #03c75a; }
    .board-content { flex-grow: 1; }
    
    /* 추가된 스타일 */
    .user-info-area {
        border: 1px solid #ddd;
        padding: 10px;
        margin-bottom: 20px;
        text-align: right;
    }
    .login-form-box {
        border: 1px solid #ddd;
        padding: 15px;
        float: right;
        width: 250px;
        margin-left: 20px;
    }
    .login-form-box p {
        margin: 5px 0;
    }
</style>
</head>
<body>
    <div class="board-container">
        <nav class="category-menu">
            <h3>카테고리</h3>
            <ul>
                <li><a href="list.do" class="${empty param.category ? 'active' : ''}">전체보기</a></li>
                <li><a href="list.do?category=자유게시판" class="${param.category eq '자유게시판' ? 'active' : ''}">자유게시판</a></li>
                <li><a href="list.do?category=질문게시판" class="${param.category eq '질문게시판' ? 'active' : ''}">질문게시판</a></li>
                <%-- 다른 카테고리 추가 가능 --%>
            </ul>
        </nav>

        <div class="board-content">
            <h2>게시판 목록 <c:if test="${not empty category}">- ${category}</c:if></h2>

            <div class="user-info-area">
                <c:choose>
                    <c:when test="${not empty sessionScope.userId}">
                        ${sessionScope.userNickname}님 환영합니다.
                        <a href="<c:url value='/board/write.do' />">[글쓰기]</a>
                        <a href="<c:url value='/member/edit.do?id=${sessionScope.userId}' />">[정보 수정]</a>
                        <a href="<c:url value='/member/logout.do' />">[로그아웃]</a>
                        <c:if test="${sessionScope.userAdmin eq 1}">
                            | <a href="<c:url value='/member/list.do' />">[회원 관리]</a>
                        </c:if>
                    </c:when>
                    <c:otherwise>
                        <form action="<c:url value='/member/login.do' />" method="post" class="login-form-box">
                            <p>
                                아이디: <input type="text" name="id" required style="width: 150px;">
                            </p>
                            <p>
                                패스워드: <input type="password" name="pass" required style="width: 150px;">
                            </p>
                            <p style="color:red; text-align: center;">${loginError}</p>
                            <p style="text-align: center;">
                                <input type="submit" value="로그인">
                                <a href="<c:url value='/member/join.do' />">회원가입</a>
                            </p>
                        </form>
                    </c:otherwise>
                </c:choose>
            </div>

            <table border="1" width="90%">
                <tr>
                    <th width="10%">번호</th>
                    <th width="*">제목</th>
                    <th width="15%">작성자</th>
                    <th width="10%">조회수</th>
                    <th width="15%">작성일</th>
                </tr>
                <c:choose>
                    <c:when test="${empty boardList}">
                        <tr>
                            <td colspan="5" align="center">등록된 게시물이 없습니다.</td>
                        </tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${boardList}" var="row" varStatus="loop">
                            <tr>
                                <td>${map.totalCount - (((map.pageNum - 1) * map.pageSize) + loop.index)}</td>
                                <td align="left">
                                    <a href="view.do?num=${row.num}">${row.title}</a>
                                </td>
                                <td>${row.nickname}</td>
                                <td>${row.visitcount}</td>
                                <td>${row.postdate}</td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </table>

            <div class="bottom-menu" style="width: 90%; margin-top: 20px; text-align: center;">
                <div class="pagination" style="margin-bottom: 10px;">
                    ${map.pagingImg}
                </div>
                
                <div class="search-area">
                    <form method="get" style="display: inline;">
                        <input type="hidden" name="category" value="${param.category}">
                        <select name="searchField">
                            <option value="title" ${map.searchField eq 'title' ? 'selected' : ''}>제목</option>
                            <option value="content" ${map.searchField eq 'content' ? 'selected' : ''}>내용</option>
                            <option value="nickname" ${map.searchField eq 'nickname' ? 'selected' : ''}>작성자</option>
                        </select>
                        <input type="text" name="searchWord" value="${map.searchWord}" />
                        <input type="submit" value="검색하기" />
                        <button type="button" onclick="location.href='write.do';">글쓰기</button>
                    </form>
                </div>
            </div>
        </div>
    </div>
</body>
</html> 