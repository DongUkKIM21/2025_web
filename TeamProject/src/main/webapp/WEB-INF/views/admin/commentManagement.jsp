<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1.0">
<title>댓글 관리</title>

<!-- Bootstrap 5 CSS -->
<link href="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/css/bootstrap.min.css" rel="stylesheet">
<!-- DataTables & Extensions CSS -->
<link rel="stylesheet" href="https://cdn.datatables.net/2.0.8/css/dataTables.bootstrap5.css">
<link rel="stylesheet" href="https://cdn.datatables.net/buttons/3.0.2/css/buttons.bootstrap5.css">

<style>
    body { display: flex; background-color: #f4f7f6; }
    .sidebar { width: 260px; background-color: #212529; color: white; min-height: 100vh; padding: 15px; }
    .sidebar .sidebar-header { font-size: 1.5rem; font-weight: bold; text-align: center; margin-bottom: 20px; padding-bottom: 15px; border-bottom: 1px solid #444; }
    .sidebar .nav-link { color: #adb5bd; font-size: 1rem; padding: 10px 15px; border-radius: 5px; }
    .sidebar .nav-link.active { background-color: #0d6efd; color: white; }
    .sidebar .nav-link:hover { background-color: #343a40; color: white; }
    .main-content { flex-grow: 1; padding: 25px; }
    .card-header { font-weight: bold; }
    .dt-buttons .btn-secondary { background-color: #6c757d; border-color: #6c757d; }
    .comment-content { max-width: 400px; overflow: hidden; text-overflow: ellipsis; white-space: nowrap; }
</style>
</head>
<body>
<div class="sidebar">
    <h1 class="sidebar-header">서비스 관리자</h1>
    <ul class="nav flex-column">
        <li class="nav-item">
            <a class="nav-link" href="<c:url value="/board/management.do"/>">게시물 관리</a>
        </li>
        <li class="nav-item">
            <a class="nav-link active" href="<c:url value="/board/commentManagement.do"/>">댓글 관리</a>
        </li>
        <li class="nav-item">
            <a class="nav-link" href="<c:url value="/member/list.do"/>">회원 관리</a>
        </li>
    </ul>
</div>

<div class="main-content">
    <div class="container-fluid">
        <h2>댓글 관리</h2>
        <div class="card">
            <div class="card-header">
                댓글 관리 대시보드
            </div>
            <div class="card-body">
                <table id="commentTable" class="table table-striped" style="width:100%">
                    <thead>
                        <tr>
                            <th>댓글 ID</th>
                            <th>게시물 제목</th>
                            <th>내용</th>
                            <th>작성자</th>
                            <th>작성일</th>
                            <th>관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${commentList}" var="comment">
                            <tr>
                                <td>${comment.cno}</td>
                                <td><a href="<c:url value="/board/view.do?num=${comment.bno}"/>" target="_blank">${comment.board_title}</a></td>
                                <td class="comment-content" title="${comment.content}">${comment.content}</td>
                                <td>${comment.nickname}</td>
                                <td>
                                    <c:if test="${not empty comment.postdate}">
                                        <fmt:formatDate value="${comment.postdate}" pattern="yyyy.MM.dd HH:mm" />
                                    </c:if>
                                </td>
                                <td>
                                    <a href="#" onclick="confirmDelete('${comment.cno}', '${comment.bno}')" class="btn btn-danger btn-sm">삭제</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<!-- jQuery -->
<script src="https://code.jquery.com/jquery-3.7.1.js"></script>
<!-- Bootstrap 5 JS -->
<script src="https://cdn.jsdelivr.net/npm/bootstrap@5.3.3/dist/js/bootstrap.bundle.min.js"></script>
<!-- DataTables JS -->
<script src="https://cdn.datatables.net/2.0.8/js/dataTables.js"></script>
<script src="https://cdn.datatables.net/2.0.8/js/dataTables.bootstrap5.js"></script>
<script src="https://cdn.datatables.net/buttons/3.0.2/js/dataTables.buttons.js"></script>
<script src="https://cdn.datatables.net/buttons/3.0.2/js/buttons.bootstrap5.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/jszip/3.10.1/jszip.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.2.7/pdfmake.min.js"></script>
<script src="https://cdnjs.cloudflare.com/ajax/libs/pdfmake/0.2.7/vfs_fonts.js"></script>
<script src="https://cdn.datatables.net/buttons/3.0.2/js/buttons.html5.min.js"></script>
<script src="https://cdn.datatables.net/buttons/3.0.2/js/buttons.print.min.js"></script>
<script src="https://cdn.datatables.net/buttons/3.0.2/js/buttons.colVis.min.js"></script>

<script>
    function confirmDelete(cno, bno) {
        if (confirm("댓글 ID " + cno + " (을)를 정말로 삭제하시겠습니까?")) {
            // 삭제 후 현재 댓글 관리 페이지로 돌아오도록 URL을 구성합니다.
            const redirectUrl = encodeURIComponent(window.location.href);
            location.href = `<c:url value="/board/deleteComment.do?cno="/>${cno}&bno=${bno}&redirect=admin`;
        }
    }

    $(document).ready(function() {
        $('#commentTable').DataTable({
            layout: {
                topStart: {
                    buttons: ['copy', 'csv', 'excel', 'pdf', 'print', 'colvis']
                }
            },
            order: [[ 0, 'desc' ]] // 0번째 컬럼(댓글 ID)을 기준으로 내림차순 정렬
        });
    });
</script>
</body>
</html> 