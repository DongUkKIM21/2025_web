<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>

<jsp:include page="../admin/admin_header.jsp">
    <jsp:param name="title" value="회원 관리"/>
</jsp:include>

<div class="main-content">
    <div class="container-fluid">
        <h2>회원 관리</h2>
        <div class="card">
            <div class="card-header d-flex justify-content-between align-items-center">
                <span>회원 관리 대시보드</span>
                <a href="<c:url value='/board/list.do'/>" class="btn btn-outline-secondary btn-sm">메인 게시판으로 돌아가기</a>
            </div>
            <div class="card-body">
                <table id="memberTable" class="table table-striped" style="width:100%">
                    <thead>
                        <tr>
                            <th>유저 ID</th>
                            <th>이름</th>
                            <th>닉네임</th>
                            <th>이메일</th>
                            <th>가입일</th>
                            <th>관리</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${memberList}" var="member">
                            <tr>
                                <td>${member.id}</td>
                                <td>${member.name}</td>
                                <td>${member.nickname}</td>
                                <td>${member.email}</td>
                                <td>
                                    <c:if test="${not empty member.regidate}">
                                        <fmt:formatDate value="${member.regidate}" pattern="yyyy.MM.dd HH:mm" />
                                    </c:if>
                                </td>
                                <td>
                                    <c:if test="${member.admin != 1}">
                                        <a href="adminEdit.do?id=${member.id}" class="btn btn-primary btn-sm">수정</a>
                                        <a href="#" onclick="confirmDelete('${member.id}', '${member.nickname}')" class="btn btn-danger btn-sm">삭제</a>
                                    </c:if>
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
    function confirmDelete(id, nickname) {
        if (confirm("'" + nickname + "' 회원을 정말로 삭제하시겠습니까? 관련 게시물도 모두 삭제됩니다.")) {
            location.href = 'delete.do?id=' + id;
        }
    }

    $(document).ready(function() {
        $('#memberTable').DataTable({
            layout: {
                topStart: {
                    buttons: ['copy', 'csv', 'excel', 'pdf', 'print', 'colvis']
                }
            }
        });
    });
</script>

<jsp:include page="../admin/admin_footer.jsp" /> 