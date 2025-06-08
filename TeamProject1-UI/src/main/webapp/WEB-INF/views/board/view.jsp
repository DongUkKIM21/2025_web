<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>

<!DOCTYPE html>
<html lang="ko">
<head>
<meta charset="UTF-8">
<title>${dto.title} - My Community</title>
<link rel="stylesheet" type="text/css" href="${pageContext.request.contextPath}/resources/css/style.css">
<script src="https://ajax.googleapis.com/ajax/libs/jquery/3.5.1/jquery.min.js"></script>
<style>
    body { background-color: #f9f9f9; font-family: 'Malgun Gothic', sans-serif; }
    .view-container { max-width: 800px; margin: 20px auto; padding: 30px; background-color: #fff; border: 1px solid #ddd; }
    .post-header .category { color: #007bff; font-weight: bold; }
    .post-header .title { font-size: 24px; font-weight: bold; margin: 10px 0; }
    .post-meta { display: flex; align-items: center; color: #888; font-size: 14px; border-bottom: 1px solid #eee; padding-bottom: 15px; }
    .post-meta .author { font-weight: bold; color: #333; }
    .post-meta .separator { margin: 0 10px; }
    .post-content { padding: 30px 0; min-height: 200px; font-size: 16px; line-height: 1.8; }
    .post-actions { display: flex; justify-content: space-between; align-items: center; margin-bottom: 30px; }
    .post-actions .btn { display: inline-block; padding: 10px 20px; border: 1px solid #ccc; text-decoration: none; color: #333; margin: 0 5px; border-radius: 5px; }
    .post-actions .btn-like { background-color: #007bff; color: white; border-color: #007bff; }
    .attachment-box { background-color: #f9f9f9; border: 1px solid #eee; padding: 15px; margin-bottom: 20px; }
    .attachment-box a { text-decoration: none; color: #007bff; }
    /* Comments */
    .comments-section { margin-top: 30px; }
    .comment { border-bottom: 1px solid #f0f0f0; padding: 15px 0; }
    .comment-author { font-weight: bold; }
    .comment-date { float: right; color: #999; font-size: 13px; }
    .comment-content { margin: 8px 0 0 0; }
    .comment-form textarea { width: 100%; padding: 10px; border: 1px solid #ddd; border-radius: 5px; box-sizing: border-box; }
    .comment-form .submit-wrapper { text-align: right; margin-top: 10px; }
    .comment-form button { padding: 8px 15px; background-color: #333; color: white; border: none; border-radius: 5px; cursor: pointer; }
</style>
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
                    $("#likeCount").text(data.likeCount);
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
    <div class="view-container">
        <div class="post-header">
            <span class="category">[${dto.category}]</span>
            <h1 class="title">${dto.title}</h1>
        </div>
        <div class="post-meta">
            <span class="author">${dto.id}</span>
            <span class="separator">|</span>
            <span class="date"><fmt:formatDate value="${dto.postdate}" pattern="yyyy.MM.dd HH:mm" /></span>
            <span class="separator">|</span>
            <span class="views">조회 ${dto.visitcount}</span>
            <span class="separator">|</span>
            <span class="likes">추천 <span id="likeCount">${dto.like_count}</span></span>
        </div>
        <div class="post-content">
            <c:if test="${not empty fileList}">
                <div class="attachment-box">
                <c:forEach items="${fileList}" var="file">
                    <c:set var="lowerCaseFileName" value="${file.original_file_name.toLowerCase()}" />
                    <c:choose>
                        <c:when test="${lowerCaseFileName.endsWith('.jpg') or lowerCaseFileName.endsWith('.jpeg') or lowerCaseFileName.endsWith('.png') or lowerCaseFileName.endsWith('.gif') or lowerCaseFileName.endsWith('.jfif')}">
                            <div>
                                <img src="${pageContext.request.contextPath}/uploads/${file.stored_file_name}" alt="${file.original_file_name}" style="max-width: 100%; height: auto; margin-bottom: 10px;">
                                <br>
                                <a href="download.do?filename=${file.stored_file_name}&original=${file.original_file_name}">
                                    ${file.original_file_name}
                                </a>
                                ( <fmt:formatNumber value="${file.file_size / 1024}" maxFractionDigits="0"/> KB)
                            </div>
                        </c:when>
                        <c:otherwise>
                             <div>
                               <a href="download.do?filename=${file.stored_file_name}&original=${file.original_file_name}">
                                    ${file.original_file_name}
                               </a>
                               ( <fmt:formatNumber value="${file.file_size / 1024}" maxFractionDigits="0"/> KB)
                            </div>
                        </c:otherwise>
                    </c:choose>
                </c:forEach>
                </div>
            </c:if>
            ${dto.content}
        </div>
        <div class="post-actions">
            <div>
                <c:if test="${not empty sessionScope.userId and (sessionScope.userId eq dto.id or sessionScope.userAdmin eq 1)}">
                    <a href="edit.do?num=${dto.num}" class="btn">수정</a>
                    <a href="#" onclick="deletePost('${dto.num}')" class="btn">삭제</a>
                </c:if>
            </div>
            <div>
                <a href="#" id="likeBtn" class="btn btn-like">추천</a>
                <a href="list.do" class="btn">목록</a>
            </div>
        </div>
        
        <div class="comments-section">
            <h4>댓글 <c:if test="${not empty commentList}">${commentList.size()}</c:if></h4>
            
            <c:choose>
                <c:when test="${not empty commentList}">
                    <c:forEach items="${commentList}" var="comment">
                        <div class="comment">
                            <strong class="comment-author">${comment.nickname}</strong>
                            <span class="comment-date">
                                <fmt:formatDate value="${comment.postdate}" pattern="yyyy.MM.dd HH:mm"/>
                                <c:if test="${not empty sessionScope.userId and (sessionScope.userId eq comment.id or sessionScope.userAdmin eq 1)}">
                                     <a href="deleteComment.do?cno=${comment.cno}&bno=${dto.num}" style="margin-left: 10px; text-decoration: none; color: #dc3545;">삭제</a>
                                </c:if>
                            </span>
                            <p class="comment-content">${comment.content}</p>
                        </div>
                    </c:forEach>
                </c:when>
                <c:otherwise>
                    <p>작성된 댓글이 없습니다.</p>
                </c:otherwise>
            </c:choose>

            <c:if test="${not empty sessionScope.userId}">
                <form action="addComment.do" method="post" class="comment-form">
                    <input type="hidden" name="bno" value="${dto.num}">
                    <textarea name="content" rows="4" placeholder="댓글을 입력하세요..." required></textarea>
                    <div class="submit-wrapper">
                        <button type="submit">등록</button>
                    </div>
                </form>
            </c:if>
        </div>
    </div>
</body>
</html> 