<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>관리자 회원 정보 수정</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/style.css">
<style>
    body { font-family: 'Malgun Gothic', sans-serif; }
    .container { width: 600px; margin: 50px auto; }
    .info-section { display: flex; justify-content: space-between; align-items: center; border-bottom: 1px solid #eee; padding: 20px 10px; }
    .info-section .title { font-weight: bold; color: #333; width: 100px; }
    .info-section .value { flex-grow: 1; color: #555; }
    .info-section .action button { background-color: #f0f0f0; border: 1px solid #ddd; padding: 5px 15px; cursor: pointer; border-radius: 4px; }
    .edit-form { display: none; padding: 15px; background-color: #f9f9f9; border-bottom: 1px solid #eee; }
    .edit-form form { display: flex; gap: 10px; align-items: center; }
    .edit-form input { padding: 8px; border: 1px solid #ccc; border-radius: 4px; flex-grow: 1; }
    .edit-form button { padding: 8px 15px; border-radius: 4px; border: none; cursor: pointer; }
    .edit-form .btn-save { background-color: #03c75a; color: white; }
    .edit-form .btn-cancel { background-color: #f0f0f0; }
    .error-message { color: red; font-size: 0.9em; margin-bottom: 10px; padding-left: 10px; }
</style>
</head>
<body>
    <div class="container">
        <h2>관리자 회원 정보 수정</h2>
        <p>[${member.nickname}] 님의 정보를 수정합니다.</p>
        
        <c:if test="${not empty errorMessage}">
			<p class="error-message">${errorMessage}</p>
		</c:if>

        <!-- 아이디 -->
        <div class="info-section">
            <span class="title">아이디</span>
            <span class="value">${member.id}</span>
            <div class="action"><button onclick="toggleEdit('id')">변경</button></div>
        </div>
        <div id="edit-form-id" class="edit-form">
            <form action="adminUpdateId.do" method="post">
                <input type="hidden" name="originalId" value="${member.id}">
                <input type="text" name="id" value="${member.id}" required>
                <button type="submit" class="btn-save">저장</button>
                <button type="button" class="btn-cancel" onclick="toggleEdit('id')">취소</button>
            </form>
        </div>

        <!-- 비밀번호 -->
        <div class="info-section">
            <span class="title">비밀번호</span>
            <span class="value">********</span>
            <div class="action"><button onclick="toggleEdit('pass')">변경</button></div>
        </div>
        <div id="edit-form-pass" class="edit-form">
            <form action="adminUpdatePassword.do" method="post">
                <input type="hidden" name="id" value="${member.id}">
                <input type="password" name="pass" placeholder="새 비밀번호 (입력 시 변경)" required>
                <button type="submit" class="btn-save">저장</button>
                <button type="button" class="btn-cancel" onclick="toggleEdit('pass')">취소</button>
            </form>
        </div>

        <!-- 이름 -->
        <div class="info-section">
            <span class="title">이름</span>
            <span class="value">${member.name}</span>
            <div class="action"><button onclick="toggleEdit('name')">변경</button></div>
        </div>
        <div id="edit-form-name" class="edit-form">
            <form action="adminUpdateName.do" method="post">
                <input type="hidden" name="id" value="${member.id}">
                <input type="text" name="name" value="${member.name}" required>
                <button type="submit" class="btn-save">저장</button>
                <button type="button" class="btn-cancel" onclick="toggleEdit('name')">취소</button>
            </form>
        </div>
        
        <!-- 닉네임 -->
        <div class="info-section">
            <span class="title">닉네임</span>
            <span class="value">${member.nickname}</span>
            <div class="action"><button onclick="toggleEdit('nickname')">변경</button></div>
        </div>
        <div id="edit-form-nickname" class="edit-form">
            <form action="adminUpdateNickname.do" method="post">
                <input type="hidden" name="id" value="${member.id}">
                <input type="text" name="nickname" value="${member.nickname}" required>
                <button type="submit" class="btn-save">저장</button>
                <button type="button" class="btn-cancel" onclick="toggleEdit('nickname')">취소</button>
            </form>
        </div>

        <!-- 이메일 -->
        <div class="info-section">
            <span class="title">이메일</span>
            <span class="value">${member.email}</span>
            <div class="action"><button onclick="toggleEdit('email')">변경</button></div>
        </div>
        <div id="edit-form-email" class="edit-form">
            <form action="adminUpdateEmail.do" method="post">
                <input type="hidden" name="id" value="${member.id}">
                <input type="email" name="email" value="${member.email}" required>
                <button type="submit" class="btn-save">저장</button>
                <button type="button" class="btn-cancel" onclick="toggleEdit('email')">취소</button>
            </form>
        </div>
        <br>
        <button type="button" onclick="location.href='<c:url value="/member/list.do"/>'">회원 목록으로 돌아가기</button>

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
            const errorSection = "${errorSection}";
            const errorMessage = "${errorMessage}";

            if (errorSection && errorMessage) {
                const formDiv = document.getElementById('edit-form-' + errorSection);
                if (formDiv) {
                    toggleEdit(errorSection);
                    let errorP = formDiv.querySelector('.error-message');
                    if (!errorP) {
                        errorP = document.createElement('div');
                        errorP.className = 'error-message';
                        formDiv.insertBefore(errorP, formDiv.firstChild);
                    }
                    errorP.textContent = errorMessage;
                }
            }
        };
    </script>
</body>
</html> 