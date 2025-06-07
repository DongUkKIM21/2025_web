package com.example.project.controller;

import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.ArrayList;

import com.example.project.dao.BoardDAO;
import com.example.project.dao.CommentDAO;
import com.example.project.dto.BoardDO;
import com.example.project.dto.CommentDO;
import com.example.project.dto.FileDO;
import com.example.project.util.BoardPage;
import com.example.project.util.FileUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.servlet.http.Part;

@WebServlet("/board/*")
@MultipartConfig(
    maxFileSize = 1024 * 1024 * 5,  // 5MB
    maxRequestSize = 1024 * 1024 * 10 // 10MB
)
public class BoardController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BoardDAO dao;
    private CommentDAO commentDao;

    @Override
    public void init() throws ServletException {
        dao = new BoardDAO();
        commentDao = new CommentDAO();
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doProcess(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        doProcess(req, resp);
    }

    private void doProcess(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String pathInfo = req.getPathInfo();
        String action = pathInfo != null ? pathInfo.substring(1) : "list.do";
        req.setCharacterEncoding("UTF-8");

        HttpSession session = req.getSession();
        boolean isLoggedIn = session.getAttribute("userId") != null;

        if ("list.do".equals(action) || "view.do".equals(action) || "download.do".equals(action)) {
            // Allow public access
        } else {
            if (!isLoggedIn) {
                resp.sendRedirect(req.getContextPath() + "/member/login.do");
                return;
            }
        }

        try {
            switch (action) {
                case "list.do": list(req, resp); break;
                case "view.do": view(req, resp); break;
                case "write.do": write(req, resp); break;
                case "edit.do": edit(req, resp); break;
                case "delete.do": delete(req, resp); break;
                case "download.do": download(req, resp); break;
                case "addComment.do": addComment(req, resp); break;
                case "deleteComment.do": deleteComment(req, resp); break;
                case "like.do": like(req, resp); break;
                case "management.do": management(req, resp); break;
                case "commentManagement.do": commentManagement(req, resp); break;
                default: list(req, resp); break;
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> map = new HashMap<>();

        String category = req.getParameter("category");
        if (category != null && !category.trim().isEmpty()) {
            map.put("category", category);
            req.setAttribute("category", category);
        }

        String searchField = req.getParameter("searchField");
        String searchWord = req.getParameter("searchWord");
        if (searchWord != null && !searchWord.trim().isEmpty()) {
            map.put("searchField", searchField);
            map.put("searchWord", searchWord);
        }

        int totalCount = dao.selectCount(map);

        int pageSize = 10;
        int blockPage = 5;

        int pageNum = 1;
        String pageTemp = req.getParameter("pageNum");
        if (pageTemp != null && !pageTemp.equals("")) {
            pageNum = Integer.parseInt(pageTemp);
        }

        // DAO에 전달할 매개변수 계산 (OFFSET 방식)
        int offset = (pageNum - 1) * pageSize;
        map.put("offset", offset);
        map.put("pageSize", pageSize);

        List<BoardDO> boardList = dao.selectListPage(map);

        // 페이지 번호 문자열 생성 (검색어와 카테고리 포함)
        String pagingImg = BoardPage.pagingStr(totalCount, pageSize, blockPage, pageNum,
                req.getContextPath() + "/board/list.do", searchField, searchWord, category);
        
        map.put("pagingImg", pagingImg);
        map.put("totalCount", totalCount);
        map.put("pageNum", pageNum);

        // 세션에서 로그인 에러 메시지 확인 후 request에 저장
        HttpSession session = req.getSession(false);
        if (session != null && session.getAttribute("loginError") != null) {
            req.setAttribute("loginError", session.getAttribute("loginError"));
            session.removeAttribute("loginError"); // 메시지를 한 번만 보여주기 위해 세션에서 제거
        }

        req.setAttribute("boardList", boardList);
        req.setAttribute("map", map);

        req.getRequestDispatcher("/WEB-INF/views/board/list.jsp").forward(req, resp);
    }

    private void view(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String num = req.getParameter("num");
        dao.updateVisitCount(num);
        BoardDO dto = dao.selectView(num);
        List<FileDO> fileList = dao.selectFiles(num);
        
        List<CommentDO> commentList = commentDao.getComments(Integer.parseInt(num));

        req.setAttribute("dto", dto);
        req.setAttribute("fileList", fileList);
        req.setAttribute("commentList", commentList);
        req.getRequestDispatcher("/WEB-INF/views/board/view.jsp").forward(req, resp);
    }

    private void write(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if ("GET".equalsIgnoreCase(method)) {
            req.getRequestDispatcher("/WEB-INF/views/board/write.jsp").forward(req, resp);
        } else { // POST
            try {
                // Get upload path
                String uploadPath = req.getServletContext().getRealPath("/uploads");
                File uploadDir;
                if (uploadPath == null) {
                    // Fallback for when getRealPath returns null
                    uploadPath = "C:" + File.separator + "uploads";
                }
                uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) {
                    uploadDir.mkdirs();
                }

                // Populate DTO from request parameters
                BoardDO dto = new BoardDO();
                dto.setId((String) req.getSession().getAttribute("userId"));
                dto.setTitle(req.getParameter("title"));
                dto.setContent(req.getParameter("content"));
                
                String category = req.getParameter("category");
                if (category == null || category.trim().isEmpty()) {
                    category = "자유게시판"; // 기본값 설정
                }
                dto.setCategory(category);

                // File processing
                List<FileDO> fileList = new ArrayList<>();
                Collection<Part> parts = req.getParts();
                for (Part part : parts) {
                    if (part.getSubmittedFileName() != null && !part.getSubmittedFileName().isEmpty()) {
                        String originalFileName = part.getSubmittedFileName();
                        String now = new SimpleDateFormat("yyyyMMdd_HmsS").format(new Date());
                        String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
                        String newFileName = now + ext;
                        
                        part.write(uploadPath + File.separator + newFileName);

                        FileDO fileDto = new FileDO();
                        fileDto.setOriginal_file_name(originalFileName);
                        fileDto.setStored_file_name(newFileName);
                        fileList.add(fileDto);
                    }
                }
                
                // Insert into database
                int newPostNum = dao.insertWrite(dto);
                for (FileDO fileDto : fileList) {
                    fileDto.setBoard_num(newPostNum);
                    dao.insertFile(fileDto);
                }
                
                resp.sendRedirect("list.do");
            } catch (Exception e) {
                e.printStackTrace();
                throw new ServletException(e);
            }
        }
    }

    private void edit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        String numParam = req.getParameter("num");

        if ("GET".equalsIgnoreCase(method)) {
            BoardDO dto = dao.selectView(numParam);
             HttpSession session = req.getSession();
            String userId = (String) session.getAttribute("userId");
            Object userAdminObj = session.getAttribute("userAdmin");
            int userAdmin = (userAdminObj != null) ? (int) userAdminObj : 0;
            
            if (dto == null || (!dto.getId().equals(userId) && userAdmin != 1)) {
                resp.sendRedirect("list.do");
                return;
            }
            req.setAttribute("dto", dto);
            req.setAttribute("fileList", dao.selectFiles(numParam));
            req.getRequestDispatcher("/WEB-INF/views/board/edit.jsp").forward(req, resp);
        } else { // POST
            try {
                String uploadPath = req.getServletContext().getRealPath("/uploads");
                File uploadDir = new File(uploadPath);
                if (!uploadDir.exists()) uploadDir.mkdirs();
                
                BoardDO updatedDto = new BoardDO();
                List<FileDO> newFileList = new ArrayList<>();
                List<String> deleteFileIdxs = new ArrayList<>();

                Collection<Part> parts = req.getParts();
                for(Part part : parts) {
                    if (part.getSubmittedFileName() != null && !part.getSubmittedFileName().isEmpty()) {
                        String originalFileName = part.getSubmittedFileName();
                        String now = new SimpleDateFormat("yyyyMMdd_HmsS").format(new Date());
                        String ext = originalFileName.substring(originalFileName.lastIndexOf("."));
                        String newFileName = now + ext;
                        part.write(uploadPath + File.separator + newFileName);
                        
                        FileDO fileDto = new FileDO();
                        fileDto.setOriginal_file_name(originalFileName);
                        fileDto.setStored_file_name(newFileName);
                        newFileList.add(fileDto);
                    } else {
                        String fieldName = part.getName();
                        String value = new String(part.getInputStream().readAllBytes(), "UTF-8");
                        switch(fieldName) {
                            case "num": updatedDto.setNum(Integer.parseInt(value)); break;
                            case "title": updatedDto.setTitle(value); break;
                            case "content": updatedDto.setContent(value); break;
                            case "category": updatedDto.setCategory(value); break;
                            case "delete_file": deleteFileIdxs.add(value); break;
                        }
                    }
                }

                dao.updatePost(updatedDto);

                for (String fileIdx : deleteFileIdxs) {
                    FileDO fileToDelete = dao.selectFile(fileIdx);
                    if (fileToDelete != null) {
                        FileUtil.deleteFile(req, fileToDelete.getStored_file_name());
                        dao.deleteFile(fileIdx);
                    }
                }
                
                for (FileDO newFile : newFileList) {
                    newFile.setBoard_num(updatedDto.getNum());
                    dao.insertFile(newFile);
                }

                resp.sendRedirect("view.do?num=" + updatedDto.getNum());
            } catch (Exception e) {
                e.printStackTrace();
                throw new ServletException(e);
            }
        }
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String num = req.getParameter("num");

        BoardDO dto = dao.selectView(num);
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("userId");
        int userAdmin = session.getAttribute("userAdmin") != null ? (int) session.getAttribute("userAdmin") : 0;

        if (dto != null && (dto.getId().equals(userId) || userAdmin == 1)) {
            List<FileDO> filesToDelete = dao.selectFiles(num);
            for(FileDO file : filesToDelete){
                FileUtil.deleteFile(req, file.getStored_file_name());
            }
            dao.deletePost(num);
            resp.sendRedirect("list.do");
        } else {
            resp.sendRedirect("list.do");
        }
    }

    private void download(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String storedFileName = req.getParameter("filename");
        String originalFileName = req.getParameter("original");
        FileUtil.download(req, resp, storedFileName, originalFileName);
    }

    private void addComment(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession();
        
        String userId = (String) session.getAttribute("userId");
        String userNickname = (String) session.getAttribute("userNickname");
        int bno = Integer.parseInt(req.getParameter("bno"));
        String content = req.getParameter("content");

        CommentDO dto = new CommentDO();
        dto.setBno(bno);
        dto.setId(userId);
        dto.setNickname(userNickname);
        dto.setContent(content);

        commentDao.addComment(dto);

        resp.sendRedirect("view.do?num=" + bno);
    }

    private void deleteComment(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String bno = req.getParameter("bno");
        String cno = req.getParameter("cno");
        String redirect = req.getParameter("redirect");

        commentDao.deleteComment(Integer.parseInt(cno));
        
        if ("admin".equals(redirect)) {
            resp.sendRedirect(req.getContextPath() + "/board/commentManagement.do");
        } else {
            resp.sendRedirect(req.getContextPath() + "/board/view.do?num=" + bno);
        }
    }

    private void like(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String num = req.getParameter("num");
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("userId");

        if (userId == null) {
            resp.setContentType("application/json");
            resp.setCharacterEncoding("UTF-8");
            resp.getWriter().write("{\"status\": \"error\", \"message\": \"로그인이 필요합니다.\"}");
            return;
        }

        int result = dao.processLike(num, userId);
        int likeCount = dao.getLikeCount(num);

        resp.setContentType("application/json");
        resp.setCharacterEncoding("UTF-8");
        
        String status = "";
        String message = "";

        switch (result) {
            case 1: // 성공
                status = "success";
                message = "추천되었습니다.";
                break;
            case 2: // 이미 추천함
                status = "warn";
                message = "이미 추천한 게시물입니다.";
                break;
            default: // 실패
                status = "error";
                message = "회원만 추천 가능합니다.";
                break;
        }
        
        String json = String.format("{\"status\": \"%s\", \"message\": \"%s\", \"likeCount\": %d}", status, message, likeCount);
        resp.getWriter().write(json);
    }

    private void management(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        // 관리자 권한 확인
        if (session == null || session.getAttribute("userAdmin") == null || (int)session.getAttribute("userAdmin") != 1) {
            resp.sendRedirect(req.getContextPath() + "/board/list.do");
            return;
        }

        List<BoardDO> boardList = dao.selectAllPostsForAdmin();
        req.setAttribute("boardList", boardList);
        req.getRequestDispatcher("/WEB-INF/views/admin/boardManagement.jsp").forward(req, resp);
    }

    private void commentManagement(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        // 관리자 권한 확인
        if (session == null || session.getAttribute("userAdmin") == null || (int)session.getAttribute("userAdmin") != 1) {
            resp.sendRedirect(req.getContextPath() + "/board/list.do");
            return;
        }

        List<CommentDO> commentList = commentDao.selectAllCommentsForAdmin();
        req.setAttribute("commentList", commentList);
        req.getRequestDispatcher("/WEB-INF/views/admin/commentManagement.jsp").forward(req, resp);
    }
} 