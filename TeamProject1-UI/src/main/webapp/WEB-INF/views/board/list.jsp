<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>커뮤니티 게시판</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/style.css">
<style>
    body { font-family: 'Malgun Gothic', sans-serif; }
    .header {
        border-bottom: 2px solid #3b4890;
        padding: 20px 0;
    }
    .header h1 {
        font-size: 24px;
        text-align: center;
        margin: 0;
        color: #3b4890;
    }
    .container {
        display: flex;
        justify-content: space-between;
        margin-top: 20px;
        padding: 0 20px;
    }
    .main-content {
        flex-grow: 1;
        width: 100%;
    }
    .sidebar {
        width: 280px;
        margin-left: 30px;
        flex-shrink: 0;
    }
    .tabs {
        border-bottom: 1px solid #ddd;
        padding-left: 0;
        list-style: none;
        margin-bottom: 20px;
    }
    .tabs li {
        display: inline-block;
        margin-right: 10px;
    }
    .tabs li a {
        display: block;
        padding: 10px 15px;
        text-decoration: none;
        color: #333;
        border-bottom: 3px solid transparent;
    }
    .tabs li a.active {
        font-weight: bold;
        color: #3b4890;
        border-bottom: 3px solid #3b4890;
    }
    .board-controls {
        display: flex;
        justify-content: space-between;
        align-items: center;
        margin-bottom: 10px;
    }
    .login-box, .user-box {
        border: 1px solid #ddd;
        padding: 15px;
    }
    .login-box p, .user-box p {
        margin: 5px 0;
    }
    .login-box input[type="text"], .login-box input[type="password"] {
        width: 100%;
        padding: 8px;
        box-sizing: border-box;
    }
    .login-box input[type="submit"] {
        width: 100%;
        padding: 8px;
        background-color: #3b4890;
        color: white;
        border: none;
        cursor: pointer;
    }
    .login-box .join-link {
        text-align: right;
        margin-top: 10px;
        font-size: 12px;
    }
    .login-box .error-message {
        color: red;
        font-size: 12px;
        text-align: center;
        margin-bottom: 10px;
    }
    .board-table {
        width: 100%;
        border-top: 2px solid #333;
        border-collapse: collapse;
    }
    .board-table th, .board-table td {
        padding: 10px;
        border-bottom: 1px solid #ddd;
        text-align: center;
    }
    .board-table .title {
        text-align: left;
    }
    .pagination {
        text-align: center;
        margin-top: 20px;
    }
</style>
</head>
<body>
    <div class="header">
        <h1>My Community</h1>
    </div>

    <div class="container">
        <div class="main-content">
            <ul class="tabs">
                <li><a href="list.do" class="${empty param.category ? 'active' : ''}">전체글</a></li>
                <li><a href="list.do?category=자유게시판" class="${param.category == '자유게시판' ? 'active' : ''}">자유게시판</a></li>
                <li><a href="list.do?category=질문게시판" class="${param.category == '질문게시판' ? 'active' : ''}">질문게시판</a></li>
            </ul>

            <div class="board-controls">
                <div class="total-count">Total ${map.totalCount} posts</div>
                <div class="search-area">
                    <form method="get" style="display: inline;">
                        <input type="hidden" name="category" value="${param.category}">
                        <select name="searchField">
                            <option value="title" ${map.searchField eq 'title' ? 'selected' : ''}>제목</option>
                            <option value="content" ${map.searchField eq 'content' ? 'selected' : ''}>내용</option>
                            <option value="nickname" ${map.searchField eq 'nickname' ? 'selected' : ''}>작성자</option>
                        </select>
                        <input type="text" name="searchWord" value="${map.searchWord}" />
                        <input type="submit" value="검색" />
                        <button type="button" onclick="location.href='write.do';">글쓰기</button>
                    </form>
                </div>
            </div>

            <table class="board-table">
                <thead>
                    <tr>
                        <th width="8%">번호</th>
                        <th width="12%">카테고리</th>
                        <th width="*">제목</th>
                        <th width="15%">작성자</th>
                        <th width="8%">조회</th>
                        <th width="8%">추천</th>
                        <th width="15%">작성일</th>
                    </tr>
                </thead>
                <tbody>
                    <c:choose>
                        <c:when test="${empty boardList}">
                            <tr>
                                <td colspan="7" align="center">등록된 게시물이 없습니다.</td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <c:forEach items="${boardList}" var="row" varStatus="loop">
                                <tr>
                                    <td>${map.totalCount - (((map.pageNum - 1) * map.pageSize) + loop.index)}</td>
                                    <td>${row.category}</td>
                                    <td class="title">
                                        <a href="view.do?num=${row.num}">${row.title}</a>
                                    </td>
                                    <td>${row.nickname}</td>
                                    <td>${row.visitcount}</td>
                                    <td>${row.like_count}</td>
                                    <td><fmt:formatDate value="${row.postdate}" pattern="yyyy-MM-dd HH:mm" /></td>
                                </tr>
                            </c:forEach>
                        </c:otherwise>
                    </c:choose>
                </tbody>
            </table>
            
            <div class="pagination">
                ${map.pagingImg}
            </div>
        </div>

        <aside class="sidebar">
            <c:choose>
                <c:when test="${not empty sessionScope.userId}">
                    <div class="user-box" style="padding: 15px; text-align: left;">
                        <p style="margin-bottom: 10px;"><strong>${sessionScope.userNickname}</strong>님, 환영합니다.</p>
                        
                        <div>
                            <a href="<c:url value='/member/edit.do?id=${sessionScope.userId}' />" style="text-decoration: none; color: #6c757d;">정보 수정</a>
                            <span style="color: #ccc;">|</span>
                            <a href="<c:url value='/member/logout.do' />" style="text-decoration: none; color: #6c757d;">로그아웃</a>
                            
                            <c:if test="${sessionScope.userAdmin == 1}">
                                <span style="color: #ccc;">|</span>
                                <a href="<c:url value='/member/list.do' />" style="text-decoration: none; color: #dc3545; font-weight: bold;">회원 관리</a>
                            </c:if>
                        </div>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="login-box">
                        <form action="<c:url value='/member/login.do' />" method="post">
                            <p>
                                <input type="text" name="id" placeholder="아이디" required>
                            </p>
                            <p>
                                <input type="password" name="pass" placeholder="패스워드" required>
                            </p>
                            <c:if test="${not empty loginError}">
                               <p class="error-message">${loginError}</p>
                            </c:if>
                            <p>
                                <input type="submit" value="로그인">
                            </p>
                             <p class="join-link">
                                <a href="<c:url value='/member/join.do' />">회원가입</a>
                            </p>
                        </form>
                    </div>
                </c:otherwise>
            </c:choose>
        </aside>
    </div>
</body>
</html> 