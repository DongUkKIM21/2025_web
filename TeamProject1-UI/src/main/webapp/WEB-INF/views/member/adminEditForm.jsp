<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>내 정보 수정</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/style.css">
<style>
    body { font-family: 'Malgun Gothic', sans-serif; background-color: #f8f9fa; }
    .container { 
        width: 600px; 
        margin: 50px auto; 
        background-color: #fff;
        padding: 40px;
        border-radius: 8px;
        box-shadow: 0 4px 15px rgba(0,0,0,0.1);
    }
    .container h2 { text-align: center; margin-bottom: 10px; }
    .container p { text-align: center; color: #666; margin-bottom: 40px; }

    .info-section { display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #eee; padding: 15px 5px; }
    .info-section .title { font-weight: bold; color: #333; width: 120px; }
    .info-section .value { flex-grow: 1; color: #555; }
    .info-section .action button { background-color: #f0f0f0; border: 1px solid #ddd; padding: 5px 15px; cursor: pointer; border-radius: 4px; }
    .info-section .action button:hover { background-color: #e9e9e9; }
    
    .edit-form { display: none; padding: 20px; background-color: #f9f9f9; border: 1px solid #eee; border-radius: 5px; margin-top: 10px; margin-bottom: 10px; }
    .edit-form form { display: flex; flex-direction: column; gap: 10px; }
    .edit-form .input-group { display: flex; gap: 10px; }
    .edit-form input { padding: 10px; border: 1px solid #ccc; border-radius: 4px; flex-grow: 1; font-size: 14px; }
    .edit-form .button-group { display: flex; gap: 10px; justify-content: flex-end; }
    .edit-form button { padding: 8px 15px; border-radius: 4px; border: none; cursor: pointer; }
    .edit-form .btn-save { background-color: #3b4890; color: white; }
    .edit-form .btn-cancel { background-color: #ccc; }
    
    .error-message { color: red; font-size: 0.9em; margin-bottom: 10px; padding-left: 5px; }
    .btn-return { display: block; width: 100%; text-align: center; margin-top: 30px; padding: 12px; background-color: #6c757d; color: white; text-decoration: none; border-radius: 5px; }
</style>
</head>
<body>
    <div class="container">
        <h2>내 정보</h2>
        <p>개인정보를 안전하게 관리하세요.</p>

        <!-- 아이디 -->
        <div class="info-section">
            <span class="title">아이디</span>
            <span class="value">${member.id}</span>
        </div>

        <!-- 비밀번호 -->
        <div class="info-section">
            <span class="title">비밀번호</span>
            <span class="value">********</span>
            <div class="action"><button onclick="toggleEdit('pass')">수정</button></div>
        </div>
        <div id="edit-form-pass" class="edit-form">
             <div class="error-message-box"></div>
            <form action="updatePassword.do" method="post">
                <div class="input-group">
                    <input type="password" name="pass" placeholder="새 비밀번호" required>
                    <input type="password" name="pass_check" placeholder="새 비밀번호 확인" required>
                </div>
                <div class="button-group">
                    <button type="submit" class="btn-save">저장</button>
                    <button type="button" class="btn-cancel" onclick="toggleEdit('pass')">취소</button>
                </div>
            </form>
        </div>

        <!-- 이름 -->
        <div class="info-section">
            <span class="title">이름</span>
            <span class="value">${member.name}</span>
            <div class="action"><button onclick="toggleEdit('name')">수정</button></div>
        </div>
        <div id="edit-form-name" class="edit-form">
             <div class="error-message-box"></div>
            <form action="updateName.do" method="post">
                 <input type="text" name="name" value="${member.name}" required>
                <div class="button-group">
                    <button type="submit" class="btn-save">저장</button>
                    <button type="button" class="btn-cancel" onclick="toggleEdit('name')">취소</button>
                </div>
            </form>
        </div>
        
        <!-- 닉네임 -->
        <div class="info-section">
            <span class="title">닉네임</span>
            <span class="value">${member.nickname}</span>
            <div class="action"><button onclick="toggleEdit('nickname')">수정</button></div>
        </div>
        <div id="edit-form-nickname" class="edit-form">
            <div class="error-message-box"></div>
            <form action="updateNickname.do" method="post">
                <input type="text" name="nickname" value="${member.nickname}" required>
                 <div class="button-group">
                    <button type="submit" class="btn-save">저장</button>
                    <button type="button" class="btn-cancel" onclick="toggleEdit('nickname')">취소</button>
                </div>
            </form>
        </div>

        <!-- 이메일 -->
        <div class="info-section">
            <span class="title">이메일</span>
            <span class="value">${member.email}</span>
            <div class="action"><button onclick="toggleEdit('email')">수정</button></div>
        </div>
        <div id="edit-form-email" class="edit-form">
            <div class="error-message-box"></div>
            <form action="updateEmail.do" method="post">
                <input type="email" name="email" value="${member.email}" required>
                <div class="button-group">
                    <button type="submit" class="btn-save">저장</button>
                    <button type="button" class="btn-cancel" onclick="toggleEdit('email')">취소</button>
                </div>
            </form>
        </div>
        
        <a href="<c:url value="/board/list.do"/>" class="btn-return">게시판으로 돌아가기</a>

    </div>

    <script>
        function toggleEdit(section) {
            const formDiv = document.getElementById('edit-form-' + section);
            document.querySelectorAll('.edit-form').forEach(f => {
                if (f.id !== formDiv.id) {
                    f.style.display = 'none';
                }
            });
            formDiv.style.display = (formDiv.style.display === 'block') ? 'none' : 'block';
        }

        window.onload = function() {
            const errorSection = '${errorSection}';
            const errorMessage = '${errorMessage}';

            if (errorSection && errorMessage) {
                toggleEdit(errorSection);
                const formDiv = document.getElementById('edit-form-' + errorSection);
                if (formDiv) {
                    const errorBox = formDiv.querySelector('.error-message-box');
                    if (errorBox) {
                        errorBox.innerHTML = '<p class="error-message">' + errorMessage + '</p>';
                    }
                }
            }
        };
    </script>
</body>
</html> 