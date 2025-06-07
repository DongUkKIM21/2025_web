<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.example.project.dto.BoardDO" %>
<%@ page import="com.example.project.dto.FileDO" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
BoardDO dto = (BoardDO) request.getAttribute("dto");
    List<FileDO> fileList = (List<FileDO>) request.getAttribute("fileList");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판 수정</title>
<!-- Summernote CSS -->
<link href="https://cdn.jsdelivr.net/npm/summernote@0.8.18/dist/summernote-lite.min.css" rel="stylesheet">
<style>
    .note-editor {
        margin-bottom: 10px;
    }
</style>
</head>
<body>
    <h2>게시판 수정</h2>
    <form name="editFrm" method="post" action="edit.do" enctype="multipart/form-data" onsubmit="return validateForm(this);">
        <input type="hidden" name="num" value="${dto.num}" />
        <input type="hidden" name="prevOfile" value="${fileList[0].original_file_name}" />
        <input type="hidden" name="prevSfile" value="${fileList[0].stored_file_name}" />
        
        <table border="1" width="90%">
            <tr>
                <td>작성자</td>
                <td>
                    <input type="text" name="name" style="width:150px;" value="${dto.nickname}" disabled />
                </td>
            </tr>
            <tr>
                <td>카테고리</td>
                <td>
                    <select name="category" class="form-control">
                        <option value="자유게시판" ${dto.category eq '자유게시판' ? 'selected' : ''}>자유게시판</option>
                        <option value="질문게시판" ${dto.category eq '질문게시판' ? 'selected' : ''}>질문게시판</option>
                         <%-- 다른 카테고리 추가 가능 --%>
                    </select>
                </td>
            </tr>
            <tr>
                <td>제목</td>
                <td>
                    <input type="text" name="title" style="width:90%;" value="${dto.title}" />
                </td>
            </tr>
            <tr>
                <td>내용</td>
                <td>
                    <textarea id="summernote" name="content">${dto.content}</textarea>
                </td>
            </tr>
            <tr>
                <td>첨부 파일</td>
                <td>
                    <input type="file" name="attachedFile" />
                     <c:if test="${not empty fileList[0].original_file_name}">
                        <br/>
                        기존 파일: ${fileList[0].original_file_name}
                    </c:if>
                </td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <button type="submit">수정 완료</button>
                    <button type="button" onclick="location.href='list.do';">목록 보기</button>
                    <button type="button" onclick="history.back();">취소</button>
                </td>
            </tr>
        </table>
    </form>

    <!-- Summernote JS -->
    <script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
    <script src="https://cdn.jsdelivr.net/npm/summernote@0.8.18/dist/summernote-lite.min.js"></script>
    <script>
        $(document).ready(function() {
            $('#summernote').summernote({
                height: 300,
                minHeight: null,
                maxHeight: null,
                focus: true,
                toolbar: [
                    ['font', ['bold', 'underline', 'clear']],
                    ['color', ['color']],
                    ['para', ['ul', 'ol', 'paragraph']],
                    ['table', ['table']],
                    ['insert', ['link', 'picture']],
                    ['view', ['help']]
                ]
            });
        });

        function validateForm(form) {
            if (form.title.value == "") {
                alert("제목을 입력하세요.");
                form.title.focus();
                return false;
            }
            if (form.content.value == "") {
                alert("내용을 입력하세요.");
                form.content.focus();
                return false;
            }
        }

        // 폼 제출 전 summernote 내용 업데이트
        $('form').on('submit', function() {
            $('#summernote').val($('#summernote').summernote('code'));
        });
    </script>
</body>
</html> 