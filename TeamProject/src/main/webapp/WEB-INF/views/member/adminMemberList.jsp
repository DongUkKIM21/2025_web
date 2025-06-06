<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>관리자 회원 관리</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/style.css">
<script>
    function confirmDelete(id, nickname) {
        if (confirm("'" + nickname + "' 회원을 정말로 삭제하시겠습니까? 관련 게시물도 모두 삭제됩니다.")) {
            location.href = 'delete.do?id=' + id;
        }
    }
</script>
</head>
<body>
    <h2>관리자 회원 관리</h2>
    <table border="1" width="100%">
        <tr>
            <th>아이디</th>
            <th>이름</th>
            <th>닉네임</th>
            <th>이메일</th>
            <th>가입일</th>
            <th>관리</th>
        </tr>
        <c:forEach items="${memberList}" var="member">
            <tr>
                <td>${member.id}</td>
                <td>${member.name}</td>
                <td>${member.nickname}</td>
                <td>${member.email}</td>
                <td>${member.regidate}</td>
                <td>
                    <c:if test="${member.admin != 1}">
                        <a href="adminEdit.do?id=${member.id}">수정</a> |
                        <a href="#" onclick="confirmDelete('${member.id}', '${member.nickname}')">삭제</a>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
    </table>
    <br>
    <button type="button" onclick="location.href='<c:url value="/board/list.do"/>'">게시판으로 돌아가기</button>
</body>
</html> 