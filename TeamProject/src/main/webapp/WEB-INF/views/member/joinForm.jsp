<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입</title>
</head>
<body>
    <h2>회원가입</h2>
    <form action="join.do" method="post">
        <p>아이디: <input type="text" name="id" required></p>
        <p>비밀번호: <input type="password" name="pass" required></p>
        <p>이름: <input type="text" name="name" required></p>
        <p>이메일: <input type="email" name="email" required></p>
        <p style="color:red;">${joinError}</p>
        <p><input type="submit" value="회원가입"></p>
    </form>
</body>
</html> 