<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="admin_header.jsp">
    <jsp:param name="title" value="댓글 관리"/>
</jsp:include>

<div class="main-content">
    <div class="container-fluid">
        <h2>댓글 관리</h2>
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span>댓글 관리 대시보드</span>
                <a href="<c:url value='/board/list.do'/>" class="btn btn-outline-secondary btn-sm">메인 게시판으로 돌아가기</a>
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
                                    <a href="#" onclick="confirmCommentDelete('${comment.cno}', '${comment.bno}')" class="btn btn-danger btn-sm">삭제</a>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>
</div>

<script>
    function confirmCommentDelete(cno, bno) {
        if (confirm("댓글 ID " + cno + " (을)를 정말로 삭제하시겠습니까?")) {
            location.href = '<c:url value="/board/deleteComment.do"/>?cno=' + cno + '&bno=' + bno + '&redirect=admin';
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

<jsp:include page="admin_footer.jsp" /> 