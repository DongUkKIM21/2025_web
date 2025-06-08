<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<div class="sidebar">
    <h1 class="sidebar-header">서비스 관리자</h1>
    <ul class="nav flex-column">
        <li class="nav-item">
            <a class="nav-link ${fn:endsWith(pageContext.request.requestURI, 'management.do') ? 'active' : ''}" href="<c:url value="/board/management.do"/>">게시물 관리</a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${fn:endsWith(pageContext.request.requestURI, 'commentManagement.do') ? 'active' : ''}" href="<c:url value="/board/commentManagement.do"/>">댓글 관리</a>
        </li>
        <li class="nav-item">
            <a class="nav-link ${fn:endsWith(pageContext.request.requestURI, 'list.do') || fn:endsWith(pageContext.request.requestURI, 'adminEdit.do') ? 'active' : ''}" href="<c:url value="/member/list.do"/>">회원 관리</a>
        </li>
    </ul>
</div> 