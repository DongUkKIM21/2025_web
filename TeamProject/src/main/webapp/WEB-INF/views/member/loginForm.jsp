<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>로그인</title>
</head>
<body>
    <h2>로그인</h2>
    <form action="login.do" method="post">
        <p>
            아이디: <input type="text" name="id" required>
        </p>
        <p>
            패스워드: <input type="password" name="pass" required>
        </p>
        <p style="color:red;">${loginError}</p>
        <p>
            <input type="submit" value="로그인">
            <a href="join.do">회원가입</a>
            <a href="${pageContext.request.contextPath}/board/list.do">게시판 가기</a>
        </p>
    </form>
</body>
</html> 