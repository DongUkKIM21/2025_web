<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>회원가입</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/style.css">
<style>
    body { font-family: 'Malgun Gothic', sans-serif; background-color: #f8f9fa; }
    .container { 
        width: 500px; 
        margin: 50px auto; 
        background-color: #fff;
        padding: 30px;
        border-radius: 8px;
        box-shadow: 0 2px 10px rgba(0,0,0,0.1);
    }
    .container h2 {
        text-align: center;
        margin-bottom: 30px;
    }
    .join-form .form-group {
        margin-bottom: 15px;
    }
    .join-form .form-group label {
        display: block;
        margin-bottom: 5px;
        font-weight: bold;
    }
    .join-form .form-group input {
        width: 100%;
        padding: 10px;
        border: 1px solid #ccc;
        border-radius: 4px;
        box-sizing: border-box;
    }
    .join-form .btn-join {
        width: 100%;
        padding: 12px;
        background-color: #3b4890;
        color: white;
        border: none;
        cursor: pointer;
        border-radius: 4px;
        font-size: 16px;
        margin-top: 20px;
    }
    .error-message {
        color: red;
        font-size: 0.9em;
        text-align: center;
        margin-top: 15px;
    }
</style>
</head>
<body>
    <div class="container">
        <h2>회원가입</h2>
        <form class="join-form" method="post" action="join.do">
            <div class="form-group">
                <label for="id">아이디</label>
                <input type="text" id="id" name="id" required>
            </div>
            <div class="form-group">
                <label for="pass">비밀번호</label>
                <input type="password" id="pass" name="pass" required>
            </div>
            <div class="form-group">
                <label for="name">이름</label>
                <input type="text" id="name" name="name" required>
            </div>
            <div class="form-group">
                <label for="nickname">닉네임</label>
                <input type="text" id="nickname" name="nickname" required>
            </div>
            <div class="form-group">
                <label for="email">이메일</label>
                <input type="email" id="email" name="email" required>
            </div>
            
            <input type="submit" class="btn-join" value="가입하기">

            <c:if test="${not empty joinError}">
                <p class="error-message">${joinError}</p>
            </c:if>
        </form>
    </div>
</body>
</html> 