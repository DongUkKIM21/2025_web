<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <h2>회원가입</h2>
    <form method="post" action="join.do">
        <table border="1" width="50%">
            <tr>
                <td>아이디</td>
                <td><input type="text" name="id" required></td>
            </tr>
            <tr>
                <td>비밀번호</td>
                <td><input type="password" name="pass" required></td>
            </tr>
            <tr>
                <td>이름</td>
                <td><input type="text" name="name" required></td>
            </tr>
            <tr>
                <td>닉네임</td>
                <td><input type="text" name="nickname" required></td>
            </tr>
            <tr>
                <td>이메일</td>
                <td><input type="email" name="email" required></td>
            </tr>
        </table>
        <br>
        <input type="submit" value="회원가입">
    </form>
    <p style="color:red;">${joinError}</p>
</body>
</html> 