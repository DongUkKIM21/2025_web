package com.example.project.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.example.project.dao.BoardDAO;
import com.example.project.dto.BoardDTO;
import com.example.project.dto.FileDTO;
import com.example.project.util.BoardPage;
import com.example.project.util.FileUtil;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.MultipartConfig;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/board/*")
@MultipartConfig(
    maxFileSize = 1024 * 1024 * 5,  // 5MB
    maxRequestSize = 1024 * 1024 * 50 // 50MB
)
public class BoardController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private BoardDAO dao;

    @Override
    public void init() throws ServletException {
        dao = new BoardDAO();
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
                default: list(req, resp); break;
            }
        } catch (Exception e) {
            throw new ServletException(e);
        }
    }

    private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Map<String, Object> map = new HashMap<>();
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

        int offset = (pageNum - 1) * pageSize;
        map.put("offset", offset);
        map.put("pageSize", pageSize);

        List<BoardDTO> boardList = dao.selectListPage(map);

        String pagingImg = BoardPage.pagingStr(totalCount, pageSize, blockPage, pageNum, req.getContextPath() + "/board/list.do", searchField, searchWord);
        map.put("pagingImg", pagingImg);
        map.put("totalCount", totalCount);
        map.put("pageNum", pageNum);
        
        req.setAttribute("boardList", boardList);
        req.setAttribute("map", map);

        req.getRequestDispatcher("/WEB-INF/views/board/list.jsp").forward(req, resp);
    }

    private void view(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String num = req.getParameter("num");
        dao.updateVisitCount(num);
        BoardDTO dto = dao.selectView(num);
        List<FileDTO> fileList = dao.selectFiles(num);

        req.setAttribute("dto", dto);
        req.setAttribute("fileList", fileList);
        req.getRequestDispatcher("/WEB-INF/views/board/view.jsp").forward(req, resp);
    }

    private void write(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if ("GET".equalsIgnoreCase(method)) {
            req.getRequestDispatcher("/WEB-INF/views/board/write.jsp").forward(req, resp);
        } else { // POST
            String title = req.getParameter("title");
            String content = req.getParameter("content");
            
            BoardDTO dto = new BoardDTO();
            dto.setTitle(title);
            dto.setContent(content);
            dto.setId((String) req.getSession().getAttribute("userId"));
            
            int newPostNum = dao.insertWrite(dto);
            
            List<FileDTO> fileList = FileUtil.uploadFiles(req);
            for (FileDTO fileDto : fileList) {
                fileDto.setBoard_num(newPostNum);
                dao.insertFile(fileDto);
            }
            
            resp.sendRedirect("list.do");
        }
    }

    private void edit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        String num = req.getParameter("num");

        BoardDTO dto = dao.selectView(num);
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("userId");
        Object userAdminObj = session.getAttribute("userAdmin");
        int userAdmin = (userAdminObj != null) ? (int) userAdminObj : 0;
        
        if (dto == null || (!dto.getId().equals(userId) && userAdmin != 1)) {
            resp.sendRedirect("list.do");
            return;
        }

        if ("GET".equalsIgnoreCase(method)) {
            req.setAttribute("dto", dto);
            req.setAttribute("fileList", dao.selectFiles(num));
            req.getRequestDispatcher("/WEB-INF/views/board/edit.jsp").forward(req, resp);
        } else { // POST
            String title = req.getParameter("title");
            String content = req.getParameter("content");
            String[] deleteFiles = req.getParameterValues("delete_file");

            BoardDTO updatedDto = new BoardDTO();
            updatedDto.setNum(Integer.parseInt(num));
            updatedDto.setTitle(title);
            updatedDto.setContent(content);
            dao.updatePost(updatedDto);

            if (deleteFiles != null) {
                for (String fileIdx : deleteFiles) {
                    dao.deleteFile(fileIdx);
                }
            }
            
            List<FileDTO> newFileList = FileUtil.uploadFiles(req);
            for (FileDTO fileDto : newFileList) {
                fileDto.setBoard_num(Integer.parseInt(num));
                dao.insertFile(fileDto);
            }
            
            resp.sendRedirect("view.do?num=" + num);
        }
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String num = req.getParameter("num");

        BoardDTO dto = dao.selectView(num);
        HttpSession session = req.getSession();
        String userId = (String) session.getAttribute("userId");
        int userAdmin = session.getAttribute("userAdmin") != null ? (int) session.getAttribute("userAdmin") : 0;

        if (dto != null && (dto.getId().equals(userId) || userAdmin == 1)) {
            List<FileDTO> filesToDelete = dao.selectFiles(num);
            for(FileDTO file : filesToDelete){
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
} 