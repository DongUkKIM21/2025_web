package com.example.project.controller;

import java.io.IOException;
import java.util.List;

import com.example.project.dao.BoardDAO;
import com.example.project.dao.CommentDAO;
import com.example.project.dao.MemberDAO;
import com.example.project.dto.BoardDO;
import com.example.project.dto.CommentDO;
import com.example.project.dto.FileDO;
import com.example.project.dto.MemberDO;
import com.example.project.util.FileUtil;

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
    private BoardDAO boardDao;
    private CommentDAO commentDao;

    @Override
    public void init() throws ServletException {
        dao = new MemberDAO();
        boardDao = new BoardDAO();
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
                showEditForm(req, resp);
                break;
            case "delete.do":
                delete(req, resp);
                break;
            case "list.do":
                list(req, resp);
                break;
            case "adminEdit.do":
                adminEdit(req, resp);
                break;
            case "updateId.do":
                updateId(req, resp);
                break;
            case "updatePassword.do":
                updatePassword(req, resp);
                break;
            case "updateName.do":
                updateName(req, resp);
                break;
            case "updateNickname.do":
                updateNickname(req, resp);
                break;
            case "updateEmail.do":
                updateEmail(req, resp);
                break;
            case "adminUpdateId.do":
                adminUpdateId(req, resp);
                break;
            case "adminUpdatePassword.do":
                adminUpdatePassword(req, resp);
                break;
            case "adminUpdateName.do":
                adminUpdateName(req, resp);
                break;
            case "adminUpdateNickname.do":
                adminUpdateNickname(req, resp);
                break;
            case "adminUpdateEmail.do":
                adminUpdateEmail(req, resp);
                break;
            default:
                resp.sendRedirect(req.getContextPath() + "/member/login.do");
                break;
        }
    }

    private void login(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String method = req.getMethod();
        if ("GET".equalsIgnoreCase(method)) {
            resp.sendRedirect(req.getContextPath() + "/board/list.do");
        } else { // POST
            String id = req.getParameter("id");
            String pass = req.getParameter("pass");
            MemberDO member = dao.getMember(id, pass);
            HttpSession session = req.getSession();

            if (member != null) {
                session.setAttribute("userId", member.getId());
                session.setAttribute("userName", member.getName());
                session.setAttribute("userNickname", member.getNickname());
                session.setAttribute("userAdmin", member.getAdmin());
                session.removeAttribute("loginError");
                resp.sendRedirect(req.getContextPath() + "/board/list.do");
            } else {
                session.setAttribute("loginError", "아이디 또는 비밀번호가 일치하지 않습니다.");
                resp.sendRedirect(req.getContextPath() + "/board/list.do");
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
        if ("GET".equalsIgnoreCase(req.getMethod())) {
            req.getRequestDispatcher("/WEB-INF/views/member/joinForm.jsp").forward(req, resp);
        } else { // POST
            String id = req.getParameter("id");
            String nickname = req.getParameter("nickname");
            if (dao.checkIdExists(id)) {
                req.setAttribute("joinError", "이미 사용 중인 아이디입니다.");
                req.getRequestDispatcher("/WEB-INF/views/member/joinForm.jsp").forward(req, resp);
                return;
            }
            if (dao.checkNicknameExists(nickname)) {
                req.setAttribute("joinError", "이미 사용 중인 닉네임입니다.");
                req.getRequestDispatcher("/WEB-INF/views/member/joinForm.jsp").forward(req, resp);
                return;
            }
            MemberDO dto = new MemberDO();
            dto.setId(id);
            dto.setPass(req.getParameter("pass"));
            dto.setName(req.getParameter("name"));
            dto.setNickname(nickname);
            dto.setEmail(req.getParameter("email"));
            int result = dao.insertMember(dto);
            if (result > 0) {
                resp.sendRedirect("login.do");
            } else {
                req.setAttribute("joinError", "회원가입에 실패했습니다. 다시 시도해주세요.");
                req.getRequestDispatcher("/WEB-INF/views/member/joinForm.jsp").forward(req, resp);
            }
        }
    }
    
    private void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/member/login.do");
            return;
        }

        int loginUserAdmin = (session.getAttribute("userAdmin") != null) ? (int) session.getAttribute("userAdmin") : 0;
        if (loginUserAdmin != 1) {
            resp.sendRedirect(req.getContextPath() + "/board/list.do");
            return;
        }
        
        List<MemberDO> memberList = dao.selectMembers();
        req.setAttribute("memberList", memberList);
        req.getRequestDispatcher("/WEB-INF/views/member/adminMemberList.jsp").forward(req, resp);
    }
    
    private void adminEdit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);

        if (session == null || session.getAttribute("userId") == null) {
            req.setAttribute("loginError", "로그인이 필요합니다.");
            req.getRequestDispatcher("/WEB-INF/views/member/loginForm.jsp").forward(req, resp);
            return;
        }

        int loginUserAdmin = (session.getAttribute("userAdmin") != null) ? (int) session.getAttribute("userAdmin") : 0;
        if (loginUserAdmin != 1) {
            req.setAttribute("loginError", "관리자 권한이 없습니다.");
            req.getRequestDispatcher("/WEB-INF/views/member/loginForm.jsp").forward(req, resp);
            return;
        }

        if ("GET".equalsIgnoreCase(req.getMethod())) {
            String idToEdit = req.getParameter("id");
            MemberDO memberToEdit = dao.getMemberById(idToEdit);
            req.setAttribute("member", memberToEdit);
            req.getRequestDispatcher("/WEB-INF/views/member/adminEditForm.jsp").forward(req, resp);
        } else { // POST
            String originalId = req.getParameter("originalId");
            resp.sendRedirect(req.getContextPath() + "/member/list.do");
        }
    }

    private void delete(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        String idToDelete = req.getParameter("id");

        if (session == null || session.getAttribute("userId") == null) {
            req.setAttribute("loginError", "로그인이 필요합니다.");
            req.getRequestDispatcher("/WEB-INF/views/member/loginForm.jsp").forward(req, resp);
            return;
        }
        
        String loginUserId = (String) session.getAttribute("userId");
        int loginUserAdmin = (session.getAttribute("userAdmin") != null) ? (int) session.getAttribute("userAdmin") : 0;
        
        if (!loginUserId.equals(idToDelete) && loginUserAdmin != 1) {
            req.setAttribute("errorMessage", "삭제 권한이 없습니다.");
            req.getRequestDispatcher("/board/list.do").forward(req, resp);
            return;
        }
        
        // 1. 회원이 작성한 모든 게시물 목록을 조회하여 첨부파일 삭제
        List<BoardDO> postsToDelete = boardDao.selectPostsByMemberId(idToDelete);
        for (BoardDO post : postsToDelete) {
            List<FileDO> filesToDelete = boardDao.selectFiles(String.valueOf(post.getNum()));
            for (FileDO file : filesToDelete) {
                FileUtil.deleteFile(req, file.getStored_file_name());
            }
        }

        // 2. 회원이 작성한 모든 댓글 삭제
        commentDao.deleteCommentsByMemberId(idToDelete);
        
        // 3. 회원이 작성한 모든 게시물 삭제
        boardDao.deletePostsByMemberId(idToDelete);
        
        // 4. 회원 삭제
        int result = dao.deleteMember(idToDelete);
        
        if (loginUserAdmin == 1) {
            if (result > 0) {
                // 관리자가 다른 회원을 삭제한 경우
                if (!loginUserId.equals(idToDelete)) {
                    resp.sendRedirect(req.getContextPath() + "/member/list.do");
                } else {
                    // 관리자 스스로 탈퇴한 경우
                    session.invalidate();
                    resp.sendRedirect(req.getContextPath() + "/board/list.do");
                }
            } else {
                req.setAttribute("errorMessage", "회원 삭제에 실패했습니다.");
                req.getRequestDispatcher("/member/list.do").forward(req, resp);
            }
        } else {
            // 일반 사용자가 탈퇴한 경우
            if (result > 0) {
                session.invalidate();
                resp.sendRedirect(req.getContextPath() + "/board/list.do");
            } else {
                req.setAttribute("errorMessage", "회원 탈퇴에 실패했습니다.");
                req.getRequestDispatcher("/member/edit.do?id=" + idToDelete).forward(req, resp);
            }
        }
    }

    private void showEditForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/member/login.do");
            return;
        }
        String loginUserId = (String) session.getAttribute("userId");
        MemberDO member = dao.getMemberById(loginUserId);
        req.setAttribute("member", member);
        
        session.setAttribute("userId", member.getId());
        session.setAttribute("userName", member.getName());
        session.setAttribute("userNickname", member.getNickname());

        req.getRequestDispatcher("/WEB-INF/views/member/editForm.jsp").forward(req, resp);
    }

    private void updateId(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/member/login.do");
            return;
        }
        String currentId = (String) session.getAttribute("userId");
        String newId = req.getParameter("id");

        if (currentId.equals(newId)) {
            resp.sendRedirect("edit.do?id=" + currentId);
            return;
        }

        if (dao.checkIdExists(newId)) {
            forwardUserError(req, resp, "id", "이미 사용 중인 아이디입니다.");
            return;
        }

        boardDao.updateMemberIdInBoard(currentId, newId);
        
        MemberDO currentMember = dao.getMemberById(currentId);
        currentMember.setId(newId);
        currentMember.setPass("");

        dao.adminUpdateMember(currentId, currentMember);

        session.setAttribute("userId", newId);
        resp.sendRedirect("edit.do?id=" + newId);
    }

    private void updatePassword(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/member/login.do");
            return;
        }
        String pass = req.getParameter("pass");
        String passCheck = req.getParameter("pass_check");
        String userId = (String) session.getAttribute("userId");

        if (pass == null || pass.isEmpty() || !pass.equals(passCheck)) {
            forwardUserError(req, resp, "pass", "비밀번호가 일치하지 않습니다.");
            return;
        }

        dao.updatePassword(userId, pass);
        resp.sendRedirect("edit.do?id=" + userId);
    }

    private void updateName(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/member/login.do");
            return;
        }
        String newName = req.getParameter("name");
        String userId = (String) session.getAttribute("userId");
        dao.updateName(userId, newName);
        session.setAttribute("userName", newName);
        resp.sendRedirect("edit.do?id=" + userId);
    }

    private void updateNickname(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/member/login.do");
            return;
        }
        String userId = (String) session.getAttribute("userId");
        String newNickname = req.getParameter("nickname");

        if (dao.checkNicknameExistsForOther(newNickname, userId)) {
            forwardUserError(req, resp, "nickname", "이미 사용 중인 닉네임입니다.");
            return;
        }
        dao.updateNickname(userId, newNickname);
        session.setAttribute("userNickname", newNickname);
        resp.sendRedirect("edit.do?id=" + userId);
    }

    private void updateEmail(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        HttpSession session = req.getSession(false);
        if (session == null || session.getAttribute("userId") == null) {
            resp.sendRedirect(req.getContextPath() + "/member/login.do");
            return;
        }
        String newEmail = req.getParameter("email");
        String userId = (String) session.getAttribute("userId");
        dao.updateEmail(userId, newEmail);
        resp.sendRedirect("edit.do?id=" + userId);
    }

    private void forwardUserError(HttpServletRequest req, HttpServletResponse resp, String section, String message) throws ServletException, IOException {
        req.setAttribute("errorSection", section);
        req.setAttribute("errorMessage", message);
        showEditForm(req, resp);
    }
    
    private void sendAdminError(HttpServletRequest req, HttpServletResponse resp, String originalId, String message) throws ServletException, IOException {
        req.setAttribute("errorMessage", message);
        req.setAttribute("member", dao.getMemberById(originalId));
        req.getRequestDispatcher("/WEB-INF/views/member/adminEditForm.jsp").forward(req, resp);
    }

    private void adminForwardError(HttpServletRequest req, HttpServletResponse resp, String id, String section, String message) throws ServletException, IOException {
        req.setAttribute("errorSection", section);
        req.setAttribute("errorMessage", message);
        req.setAttribute("member", dao.getMemberById(id));
        req.getRequestDispatcher("/WEB-INF/views/member/adminEditForm.jsp").forward(req, resp);
    }

    private void adminUpdateId(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String originalId = req.getParameter("originalId");
        String newId = req.getParameter("id");

        if (dao.checkIdExists(newId)) {
            adminForwardError(req, resp, originalId, "id", "이미 사용 중인 아이디입니다.");
            return;
        }

        boardDao.updateMemberIdInBoard(originalId, newId);
        
        MemberDO member = dao.getMemberById(originalId);
        member.setId(newId);
        member.setPass("");

        dao.adminUpdateMember(originalId, member);

        resp.sendRedirect("adminEdit.do?id=" + newId);
    }

    private void adminUpdatePassword(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String id = req.getParameter("id");
        String pass = req.getParameter("pass");

        if (pass == null || pass.trim().isEmpty()) {
            adminForwardError(req, resp, id, "pass", "비밀번호를 입력해주세요.");
            return;
        }
        
        dao.updatePassword(id, pass);
        resp.sendRedirect("adminEdit.do?id=" + id);
    }

    private void adminUpdateName(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String id = req.getParameter("id");
        String newName = req.getParameter("name");
        dao.updateName(id, newName);
        resp.sendRedirect("adminEdit.do?id=" + id);
    }

    private void adminUpdateNickname(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String id = req.getParameter("id");
        String newNickname = req.getParameter("nickname");

        if (dao.checkNicknameExistsForOther(newNickname, id)) {
            adminForwardError(req, resp, id, "nickname", "이미 사용 중인 닉네임입니다.");
            return;
        }

        dao.updateNickname(id, newNickname);
        resp.sendRedirect("adminEdit.do?id=" + id);
    }

    private void adminUpdateEmail(HttpServletRequest req, HttpServletResponse resp) throws IOException, ServletException {
        String id = req.getParameter("id");
        String newEmail = req.getParameter("email");
        dao.updateEmail(id, newEmail);
        resp.sendRedirect("adminEdit.do?id=" + id);
    }
} 