<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>게시판 글쓰기 - My Community</title>
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
        box-sizing: border-box; /* padding 포함해서 width 계산 */
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
</style>
</head>
<body>
    <div class="write-container">
        <h2>게시판 글쓰기</h2>
        <form name="writeFrm" method="post" action="write.do" enctype="multipart/form-data">
            <div class="form-group">
                <label for="author">작성자</label>
                <input type="text" id="author" name="author" value="${sessionScope.userName}" disabled />
            </div>
            
            <div class="form-group">
                <label for="category">카테고리</label>
                <select id="category" name="category" class="form-control">
                    <option value="자유게시판">자유게시판</option>
                    <option value="질문게시판">질문게시판</option>
                </select>
            </div>
            
            <div class="form-group">
                <label for="title">제목</label>
                <input type="text" id="title" name="title" />
            </div>
            
            <div class="form-group">
                <label for="summernote">내용</label>
                <textarea id="summernote" name="content"></textarea>
            </div>
            
            <div class="form-group">
                <label for="attachedFile">첨부 파일</label>
                <div class="file-input-wrapper">
                    <input type="file" id="attachedFile" name="attachedFile" />
                </div>
            </div>
            
            <div class="form-actions">
                <button type="submit" class="btn btn-submit">작성 완료</button>
                <button type="reset" class="btn">다시 입력</button>
                <button type="button" class="btn btn-list" onclick="location.href='list.do';">목록 보기</button>
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
            $('form[name="writeFrm"]').on('submit', function(e) {
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