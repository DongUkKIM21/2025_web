<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판 글쓰기</title>
<!-- Summernote CSS -->
<link href="https://cdn.jsdelivr.net/npm/summernote@0.8.18/dist/summernote-lite.min.css" rel="stylesheet">
<style>
    .note-editor {
        margin-bottom: 10px;
    }
</style>
</head>
<body>
    <h2>게시판 글쓰기</h2>
    <form name="writeFrm" method="post" action="write.do" enctype="multipart/form-data" onsubmit="return validateForm(this);">
        <table border="1" width="90%">
            <tr>
                <td>작성자</td>
                <td>
                    <input type="text" name="name" style="width:150px;" value="${sessionScope.userName}" disabled />
                </td>
            </tr>
            <tr>
                <td>카테고리</td>
                <td>
                    <select name="category" class="form-control">
                        <option value="자유게시판">자유게시판</option>
                        <option value="질문게시판">질문게시판</option>
                        <%-- 다른 카테고리 추가 가능 --%>
                    </select>
                </td>
            </tr>
            <tr>
                <td>제목</td>
                <td>
                    <input type="text" name="title" style="width:90%;" />
                </td>
            </tr>
            <tr>
                <td>내용</td>
                <td>
                    <textarea id="summernote" name="content"></textarea>
                </td>
            </tr>
            <tr>
                <td>첨부 파일</td>
                <td>
                    <input type="file" name="attachedFile" />
                </td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <button type="submit">작성 완료</button>
                    <button type="reset">다시 입력</button>
                    <button type="button" onclick="location.href='list.do';">목록 보기</button>
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