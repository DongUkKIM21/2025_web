<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.example.project.dto.FileDTO" %>
<%@ page import="com.example.project.dto.BoardDTO" %>
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
                <%
                if (fileList != null && !fileList.isEmpty()) {
                    for (FileDTO file : fileList) {
                    	// Set file as an attribute to be accessible by EL
                        pageContext.setAttribute("file", file);
                %>
                        <div>
                           <a href="download.do?filename=${file.stored_file_name}&original=${file.original_file_name}">
                                ${file.original_file_name}
                           </a>
                           ( ${ (file.file_size / 1024) + 1 } KB)
                        </div>
                <%
                    }
                } else {
                %>
                    첨부파일 없음
                <%
                }
                %>
            </td>
        </tr>
        <tr>
            <td colspan="4" align="center">
                <%
                String userId = (String) session.getAttribute("userId");
                Integer userAdmin = (Integer) session.getAttribute("userAdmin");
                // 관리자는 1, 일반 사용자는 0 또는 null
                boolean isAdmin = (userAdmin != null && userAdmin == 1);

                if (dto != null && userId != null && (userId.equals(dto.getId()) || isAdmin)) {
                %>
                    <button type="button" onclick="location.href='edit.do?num=${dto.num}'">수정하기</button>
                    <button type="button" onclick="deletePost('${dto.num}')">삭제하기</button>
                <%
                }
                %>
                <button type="button" onclick="location.href='list.do'">목록 보기</button>
            </td>
        </tr>
    </table>
</body>
</html> 