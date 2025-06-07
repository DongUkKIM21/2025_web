<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ page import="java.util.List" %>
<%@ page import="com.example.project.dto.FileDO" %>
<%@ page import="com.example.project.dto.BoardDO" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%
    BoardDO dto = (BoardDO) request.getAttribute("dto");
    List<FileDO> fileList = (List<FileDO>) request.getAttribute("fileList");
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
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<script>
    function deletePost(num) {
        if (confirm("정말로 삭제하시겠습니까?")) {
            location.href = "delete.do?num=" + num;
        }
    }
    
    $(function(){
        $("#likeBtn").on("click", function(){
            $.ajax({
                url: "like.do",
                type: "post",
                data: { num: "${dto.num}" },
                dataType: "json",
                success: function(data){
                    // 서버로부터 받은 최신 추천 수로 화면을 업데이트합니다.
                    $("#likeCount").text(data.likeCount);
                    // 서버가 보내준 메시지를 알림창으로 띄웁니다.
                    alert(data.message);
                },
                error: function(){
                    alert("추천 처리 중 오류가 발생했습니다.");
                }
            });
        });
    });
</script>
</head>
<body>
    <h2>게시판 상세보기</h2>
    <table border="1" width="90%">
        <tr>
            <td>번호</td>
            <td>${dto.num}</td>
            <td>작성자</td>
            <td>${dto.nickname}</td>
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
            <td colspan="3" height="100">
                ${dto.content}
            </td>
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
                 <button type="button" id="likeBtn">추천</button>
                 <span id="likeCount">${dto.like_count}</span>
                <c:if test="${not empty sessionScope.userId and (sessionScope.userId eq dto.id or sessionScope.userAdmin eq 1)}">
                    <button type="button" onclick="location.href='edit.do?num=${dto.num}'">수정하기</button>
                    <button type="button" onclick="deletePost('${dto.num}')">삭제하기</button>
                </c:if>
                <button type="button" onclick="location.href='list.do'">목록 보기</button>
            </td>
        </tr>
    </table>

    <%-- 댓글 기능 추가 --%>
    <div style="width: 90%; margin-top: 20px;">
        <h4>댓글</h4>
        
        <!-- 댓글 목록 -->
        <c:choose>
            <c:when test="${not empty commentList}">
                <c:forEach items="${commentList}" var="comment">
                    <div style="border-bottom: 1px solid #eee; padding: 10px 0;">
                        <strong>${comment.nickname}</strong> (${comment.id})
                        <span style="float: right; color: #888; font-size: 0.9em;">
                            <c:out value="${comment.postdate}"/>
                            <c:if test="${not empty sessionScope.userId and (sessionScope.userId eq comment.id or sessionScope.userAdmin eq 1)}">
                                 <a href="deleteComment.do?cno=${comment.cno}&bno=${dto.num}" style="margin-left: 10px; text-decoration: none; color: red;">[삭제]</a>
                            </c:if>
                        </span>
                        <p style="margin: 5px 0;">${comment.content}</p>
                    </div>
                </c:forEach>
            </c:when>
            <c:otherwise>
                <p>작성된 댓글이 없습니다.</p>
            </c:otherwise>
        </c:choose>

        <!-- 댓글 작성 폼 -->
        <c:if test="${not empty sessionScope.userId}">
            <form action="addComment.do" method="post" style="margin-top: 20px;">
                <input type="hidden" name="bno" value="${dto.num}">
                <textarea name="content" rows="3" style="width: 100%;" placeholder="댓글을 입력하세요" required></textarea>
                <br>
                <button type="submit" style="float: right;">등록</button>
            </form>
        </c:if>
    </div>
</body>
</html> 