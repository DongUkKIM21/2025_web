<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="com.example.project.dto.BoardDTO" %>
<%@ page import="com.example.project.dto.FileDTO" %>
<%@ page import="java.util.List" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    BoardDTO dto = (BoardDTO) request.getAttribute("dto");
    List<FileDTO> fileList = (List<FileDTO>) request.getAttribute("fileList");
%>
<!DOCTYPE html>
<html>
<head>
<meta charset="UTF-8">
<title>게시판 수정</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/style.css">
</head>
<body>
    <h2>게시판 수정</h2>
    <form name="editForm" method="post" action="edit.do" enctype="multipart/form-data">
        <input type="hidden" name="num" value="${dto.num}" />
        <table border="1" width="90%">
            <tr>
                <td>작성자</td>
                <td><input type="text" name="id" value="${dto.id}" readonly/></td>
            </tr>
            <tr>
                <td>제목</td>
                <td><input type="text" name="title" style="width: 90%;" value="${dto.title}"/></td>
            </tr>
            <tr>
                <td>내용</td>
                <td><textarea name="content" style="width: 90%; height: 100px;">${dto.content}</textarea></td>
            </tr>
             <tr>
                <td>기존 첨부파일</td>
                <td>
                    <c:choose>
                        <c:when test="${not empty fileList}">
                            <c:forEach items="${fileList}" var="file">
                               <div>
                                  ${file.original_file_name}
                                   <input type="checkbox" name="delete_file" value="${file.idx}"> 삭제
                               </div>
                            </c:forEach>
                        </c:when>
                        <c:otherwise>
                           없음
                        </c:otherwise>
                    </c:choose>
                </td>
            </tr>
            <tr>
                <td>파일 추가 1</td>
                <td><input type="file" name="file1" /></td>
            </tr>
            <tr>
                <td>파일 추가 2</td>
                <td><input type="file" name="file2" /></td>
            </tr>
            <tr>
                <td>파일 추가 3</td>
                <td><input type="file" name="file3" /></td>
            </tr>
            <tr>
                <td colspan="2" align="center">
                    <button type="submit">수정 완료</button>
                    <button type="button" onclick="history.back();">취소</button>
                </td>
            </tr>
        </table>
    </form>
</body>
</html> 