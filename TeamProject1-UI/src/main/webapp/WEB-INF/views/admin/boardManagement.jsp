<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<jsp:include page="admin_header.jsp">
    <jsp:param name="title" value="게시물 관리"/>
</jsp:include>

<div class="main-content">
    <div class="container-fluid">
        <h2>게시물 관리</h2>
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span>게시물 관리 대시보드</span>
                <a href="<c:url value='/board/list.do'/>" class="btn btn-outline-secondary btn-sm">메인 게시판으로 돌아가기</a>
            </div>
            <div class="card-body">
                <table id="boardTable" class="table table-striped" style="width:100%">
                    <thead>
                        <tr>
                            <th>번호</th>
                            <th>카테고리</th>
                            <th>제목</th>
                            <th>작성자</th>
                            <th>작성일</th>
                            <th>조회수</th>
                            <th>추천수</th>
                            <th>관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${boardList}" var="board">
                            <tr>
                                <td>${board.num}</td>
                                <td>${board.category}</td>
                                <td><a href="<c:url value="/board/view.do?num=${board.num}"/>" target="_blank">${board.title}</a></td>
                                <td>${board.nickname}</td>
                                <td>
                                    <c:if test="${not empty board.postdate}">
                                        <fmt:formatDate value="${board.postdate}" pattern="yyyy.MM.dd HH:mm" />
                                    </c:if>
                                </td>
                                <td>${board.visitcount}</td>
                                <td>${board.like_count}</td>
                                <td>
                                    <a href="<c:url value="/board/edit.do?num=${board.num}"/>" class="btn btn-primary btn-sm">수정</a>
                                    <a href="#" onclick="confirmDelete('${board.num}', '${board.title}')" class="btn btn-danger btn-sm">삭제</a>
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
    function confirmDelete(num, title) {
        if (confirm("게시물 '" + title + "' (을)를 정말로 삭제하시겠습니까?")) {
            location.href = '<c:url value="/board/delete.do?num="/>' + num;
        }
    }

    $(document).ready(function() {
        $('#boardTable').DataTable({
            layout: {
                topStart: {
                    buttons: ['copy', 'csv', 'excel', 'pdf', 'print', 'colvis']
                }
            },
            order: [[ 0, 'desc' ]] // 0번째 컬럼(번호)을 기준으로 내림차순 정렬
        });
    });
</script>

<jsp:include page="admin_footer.jsp" /> 