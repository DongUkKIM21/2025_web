package com.example.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.project.dto.MemberDTO;

public class MemberDAO {

    /**
     * 회원가입
     */
    public int insertMember(MemberDTO dto) {
        String sql = "INSERT INTO member (id, pass, name, email) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dto.getId());
            pstmt.setString(2, dto.getPass());
            pstmt.setString(3, dto.getName());
            pstmt.setString(4, dto.getEmail());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // 실패 시
    }

    /**
     * 로그인 (회원 인증)
     */
    public MemberDTO getMember(String id, String pass) {
        String sql = "SELECT * FROM member WHERE id=? AND pass=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, pass);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    MemberDTO dto = new MemberDTO();
                    dto.setId(rs.getString("id"));
                    dto.setName(rs.getString("name"));
                    dto.setEmail(rs.getString("email"));
                    dto.setRegidate(rs.getTimestamp("regidate"));
                    dto.setAdmin(rs.getInt("admin"));
                    return dto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // 실패 시
    }

    /**
     * 회원 정보 수정
     */
    public int updateMember(MemberDTO dto) {
        // 0: 실패, 1: 성공
        // TODO: Implement JDBC logic
        return 0;
    }

    /**
     * 회원 탈퇴
     */
    public int deleteMember(String id) {
        // 0: 실패, 1: 성공
        // TODO: Implement JDBC logic
        return 0;
    }

    /**
     * (관리자) 모든 회원 목록 조회
     */
    public List<MemberDTO> selectMembers() {
        // TODO: Implement JDBC logic
        return new ArrayList<>();
    }
} 