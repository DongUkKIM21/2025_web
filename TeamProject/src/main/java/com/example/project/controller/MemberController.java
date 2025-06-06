package com.example.project.controller;

import java.io.IOException;

import com.example.project.dao.MemberDAO;
import com.example.project.dto.MemberDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/member/*")
public class MemberController extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private MemberDAO dao;

    @Override
    public void init() throws ServletException {
        dao = new MemberDAO();
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
        String action = pathInfo != null ? pathInfo.substring(1) : "login.do";
        req.setCharacterEncoding("UTF-8");

        switch (action) {
            case "login.do":
                login(req, resp);
                break;
            case "logout.do":
                logout(req, resp);
                break;
            case "join.do":
                join(req, resp);
                break;
            case "edit.do":
                edit(req, resp);
                break;
            case "delete.do":
                delete(req, resp);
                break;
            default:
                // Redirect to a main or login page
                resp.sendRedirect(req.getContextPath() + "/member/login.do");
                break;
        }
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if ("GET".equalsIgnoreCase(method)) {
            // Show login form
            req.getRequestDispatcher("/WEB-INF/views/member/loginForm.jsp").forward(req, resp);
        } else { // POST
            String id = req.getParameter("id");
            String pass = req.getParameter("pass");

            MemberDTO member = dao.getMember(id, pass);

            if (member != null) {
                // 로그인 성공
                HttpSession session = req.getSession();
                session.setAttribute("userId", member.getId());
                session.setAttribute("userName", member.getName());
                session.setAttribute("userAdmin", member.getAdmin());
                
                resp.sendRedirect(req.getContextPath() + "/board/list.do");
            } else {
                // 로그인 실패
                req.setAttribute("loginError", "아이디 또는 비밀번호가 일치하지 않습니다.");
                req.getRequestDispatcher("/WEB-INF/views/member/loginForm.jsp").forward(req, resp);
            }
        }
    }
    
    private void logout(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        HttpSession session = req.getSession(false);
        if (session != null) {
            session.invalidate();
        }
        resp.sendRedirect(req.getContextPath() + "/member/login.do");
    }

    private void join(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if ("GET".equalsIgnoreCase(method)) {
            // Show join form
            req.getRequestDispatcher("/WEB-INF/views/member/joinForm.jsp").forward(req, resp);
        } else { // POST
            MemberDTO dto = new MemberDTO();
            dto.setId(req.getParameter("id"));
            dto.setPass(req.getParameter("pass"));
            dto.setName(req.getParameter("name"));
            dto.setEmail(req.getParameter("email"));

            int result = dao.insertMember(dto);

            if (result > 0) {
                // 회원가입 성공
                resp.sendRedirect("login.do");
            } else {
                // 회원가입 실패
                req.setAttribute("joinError", "회원가입에 실패했습니다. 다시 시도해주세요.");
                req.getRequestDispatcher("/WEB-INF/views/member/joinForm.jsp").forward(req, resp);
            }
        }
    }

    private void edit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        // TODO: Implement edit logic
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        // TODO: Implement delete logic
    }
} 