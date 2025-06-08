<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>게시판 수정 - My Community</title>
<!-- Summernote CSS -->
<link href="https://cdn.jsdelivr.net/npm/summernote@0.8.18/dist/summernote-lite.min.css" rel="stylesheet">
<style>
    body { background-color: #f9f9f9; font-family: 'Malgun Gothic', sans-serif; color: #333; }
    .write-container { max-width: 800px; margin: 20px auto; padding: 30px; background-color: #fff; border: 1px solid #ddd; border-radius: 8px; }
    h2 { text-align: center; margin-bottom: 30px; }
    .form-group { margin-bottom: 20px; }
    .form-group label { display: block; font-weight: bold; margin-bottom: 8px; }
    .form-group input[type="text"],
    .form-group select {
        width: 100%;
        padding: 10px;
        border: 1px solid #ccc;
        border-radius: 4px;
        box-sizing: border-box;
    }
    .form-group .note-editor {
        border: 1px solid #ccc;
        border-radius: 4px;
    }
    .form-actions { text-align: center; margin-top: 30px; }
    .btn { display: inline-block; padding: 10px 20px; border: 1px solid #ccc; text-decoration: none; color: #333; margin: 0 5px; border-radius: 5px; cursor: pointer; background-color: #f0f0f0; }
    .btn-submit { background-color: #007bff; color: white; border-color: #007bff; }
    .btn-cancel { background-color: #6c757d; color: white; border-color: #6c757d; }
    .btn-list { background-color: #f8f9fa; color: #333; border-color: #ccc; }
    .file-input-wrapper {
        border: 1px solid #ccc;
        border-radius: 4px;
        padding: 10px;
        background-color: #fff;
    }
    .existing-file {
        margin-top: 10px;
        font-size: 14px;
        color: #555;
    }
</style>
</head>
<body>
    <div class="write-container">
        <h2>게시판 수정</h2>
        <form name="editFrm" method="post" action="edit.do" enctype="multipart/form-data">
            <input type="hidden" name="num" value="${dto.num}" />
            
            <div class="form-group">
                <label for="author">작성자</label>
                <input type="text" id="author" name="author" value="${dto.nickname}" disabled />
            </div>

            <div class="form-group">
                <label for="category">카테고리</label>
                <select id="category" name="category" class="form-control">
                    <option value="자유게시판" ${dto.category eq '자유게시판' ? 'selected' : ''}>자유게시판</option>
                    <option value="질문게시판" ${dto.category eq '질문게시판' ? 'selected' : ''}>질문게시판</option>
                </select>
            </div>

            <div class="form-group">
                <label for="title">제목</label>
                <input type="text" id="title" name="title" value="${dto.title}" />
            </div>

            <div class="form-group">
                <label for="summernote">내용</label>
                <textarea id="summernote" name="content">${dto.content}</textarea>
            </div>

            <div class="form-group">
                <label for="attachedFile">첨부 파일</label>
                <div class="file-input-wrapper">
                    <input type="file" id="attachedFile" name="attachedFile" />
                </div>
                <c:forEach items="${fileList}" var="file" varStatus="status">
                    <div class="existing-file">
                        기존 파일 ${status.count}: ${file.original_file_name}
                        <input type="checkbox" name="delete_file" value="${file.idx}" id="delete_file_${file.idx}">
                        <label for="delete_file_${file.idx}">삭제</label>
                    </div>
                </c:forEach>
            </div>

            <div class="form-actions">
                <button type="submit" class="btn btn-submit">수정 완료</button>
                <button type="button" class="btn btn-list" onclick="location.href='list.do';">목록 보기</button>
                <button type="button" class="btn btn-cancel" onclick="history.back();">취소</button>
            </div>
        </form>
    </div>

    <!-- Summernote JS and form validation script -->
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

            // Form submission handler
            $('form[name="editFrm"]').on('submit', function(e) {
                $('#summernote').val($('#summernote').summernote('code'));
                
                const form = this;
                if (form.title.value.trim() === "") {
                    alert("제목을 입력하세요.");
                    form.title.focus();
                    e.preventDefault();
                    return;
                }
                
                const content = $('#summernote').summernote('code');
                if ($('#summernote').summernote('isEmpty')) {
                    alert("내용을 입력하세요.");
                    $('#summernote').summernote('focus');
                    e.preventDefault();
                    return;
                }

                const maxPostSize = 2 * 1024 * 1024; // 2MB
                const contentByteLength = new TextEncoder().encode(content).length;

                if (contentByteLength > maxPostSize) {
                    alert("게시글 내용의 용량이 너무 큽니다. (최대 2MB)");
                    $('#summernote').summernote('focus');
                    e.preventDefault();
                }
            });
        });
    </script>
</body>
</html> 