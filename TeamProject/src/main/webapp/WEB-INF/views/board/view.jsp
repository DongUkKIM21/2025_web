<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.example.project.dto.FileDTO" %>
<%@ page import="com.example.project.dto.BoardDTO" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    BoardDTO dto = (BoardDTO) request.getAttribute("dto");
    List<FileDTO> fileList = (List<FileDTO>) request.getAttribute("fileList");
    String userId = session.getAttribute("userId") != null ? (String)session.getAttribute("userId") : "";
    Object userAdminObj = session.getAttribute("userAdmin");
    int userAdmin = 0;
    if (userAdminObj != null) {
        userAdmin = (Integer) userAdminObj;
    }
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판 상세보기</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/style.css">
<script>
    function deletePost(num) {
        if (confirm("정말로 삭제하시겠습니까?")) {
            location.href = "delete.do?num=" + num;
        }
    }
</script>
</head>
<body>
    <h2>게시판 상세보기</h2>
    <table border="1" width="90%">
        <tr>
            <td>번호</td>
            <td>${dto.num}</td>
            <td>작성자</td>
            <td>${dto.id}</td>
        </tr>
        <tr>
            <td>작성일</td>
            <td>${dto.postdate}</td>
            <td>조회수</td>
            <td>${dto.visitcount}</td>
        </tr>
        <tr>
            <td>제목</td>
            <td colspan="3">${dto.title}</td>
        </tr>
        <tr>
            <td>내용</td>
            <td colspan="3" height="100">${dto.content}</td>
        </tr>
        <tr>
            <td>첨부파일</td>
            <td colspan="3">
                <c:choose>
                    <c:when test="${not empty fileList}">
                        <c:forEach items="${fileList}" var="file">
                            <div>
                               <a href="download.do?filename=${file.stored_file_name}&original=${file.original_file_name}">
                                    ${file.original_file_name}
                               </a>
                               ( ${ (file.file_size / 1024) + 1 } KB)
                            </div>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        첨부파일 없음
                    </c:otherwise>
                </c:choose>
            </td>
        </tr>
        <tr>
            <td colspan="4" align="center">
                <c:if test="${not empty sessionScope.userId and (sessionScope.userId eq dto.id or sessionScope.userAdmin eq 1)}">
                    <button type="button" onclick="location.href='edit.do?num=${dto.num}'">수정하기</button>
                    <button type="button" onclick="deletePost('${dto.num}')">삭제하기</button>
                </c:if>
                <button type="button" onclick="location.href='list.do'">목록 보기</button>
            </td>
        </tr>
    </table>
</body>
</html> 