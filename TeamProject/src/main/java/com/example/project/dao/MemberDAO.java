package com.example.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.project.dto.MemberDO;

public class MemberDAO {

    /**
     * 회원가입
     */
    public int insertMember(MemberDO dto) {
        String sql = "INSERT INTO member (id, pass, name, email, nickname) VALUES (?, ?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dto.getId());
            pstmt.setString(2, dto.getPass());
            pstmt.setString(3, dto.getName());
            pstmt.setString(4, dto.getEmail());
            pstmt.setString(5, dto.getNickname());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0; // 실패 시
    }

    /**
     * 로그인 (회원 인증)
     */
    public MemberDO getMember(String id, String pass) {
        String sql = "SELECT * FROM member WHERE id=? AND pass=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            pstmt.setString(2, pass);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    MemberDO dto = new MemberDO();
                    dto.setId(rs.getString("id"));
                    dto.setName(rs.getString("name"));
                    dto.setNickname(rs.getString("nickname"));
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
     * 아이디 중복 확인
     */
    public boolean checkIdExists(String id) {
        String sql = "SELECT COUNT(*) FROM member WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 닉네임 중복 확인
     */
    public boolean checkNicknameExists(String nickname) {
        String sql = "SELECT COUNT(*) FROM member WHERE nickname=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nickname);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 다른 사용자의 닉네임 중복 확인
     */
    public boolean checkNicknameExistsForOther(String nickname, String userId) {
        String sql = "SELECT COUNT(*) FROM member WHERE nickname=? AND id != ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, nickname);
            pstmt.setString(2, userId);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) > 0;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * ID로 회원 정보 조회
     */
    public MemberDO getMemberById(String id) {
        String sql = "SELECT * FROM member WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    MemberDO dto = new MemberDO();
                    dto.setId(rs.getString("id"));
                    dto.setName(rs.getString("name"));
                    dto.setNickname(rs.getString("nickname"));
                    dto.setEmail(rs.getString("email"));
                    dto.setRegidate(rs.getTimestamp("regidate"));
                    dto.setAdmin(rs.getInt("admin"));
                    return dto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * (관리자) 모든 회원 목록 조회
     */
    public List<MemberDO> selectMembers() {
        List<MemberDO> memberList = new ArrayList<>();
        String sql = "SELECT * FROM member ORDER BY regidate DESC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql);
             ResultSet rs = pstmt.executeQuery()) {
            while (rs.next()) {
                MemberDO dto = new MemberDO();
                dto.setId(rs.getString("id"));
                dto.setName(rs.getString("name"));
                dto.setNickname(rs.getString("nickname"));
                dto.setEmail(rs.getString("email"));
                dto.setRegidate(rs.getTimestamp("regidate"));
                dto.setAdmin(rs.getInt("admin"));
                memberList.add(dto);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return memberList;
    }

    /**
     * 회원 정보 수정
     */
    public int updateMember(MemberDO dto) {
        String sql = "UPDATE member SET pass=?, name=?, nickname=?, email=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dto.getPass());
            pstmt.setString(2, dto.getName());
            pstmt.setString(3, dto.getNickname());
            pstmt.setString(4, dto.getEmail());
            pstmt.setString(5, dto.getId());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * (관리자) 회원 정보 수정 (ID 포함)
     */
    public int adminUpdateMember(String oldId, MemberDO dto) {
        String sql;
        // 비밀번호가 제공되었는지 확인
        boolean isPasswordProvided = dto.getPass() != null && !dto.getPass().isEmpty();
        
        if (isPasswordProvided) {
            sql = "UPDATE member SET id=?, pass=?, name=?, nickname=?, email=? WHERE id=?";
        } else {
            sql = "UPDATE member SET id=?, name=?, nickname=?, email=? WHERE id=?";
        }
        
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            int paramIndex = 1;
            pstmt.setString(paramIndex++, dto.getId());
            if (isPasswordProvided) {
                pstmt.setString(paramIndex++, dto.getPass());
            }
            pstmt.setString(paramIndex++, dto.getName());
            pstmt.setString(paramIndex++, dto.getNickname());
            pstmt.setString(paramIndex++, dto.getEmail());
            pstmt.setString(paramIndex, oldId);
            
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 이름 업데이트
     */
    public int updateName(String userId, String newName) {
        String sql = "UPDATE member SET name=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newName);
            pstmt.setString(2, userId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * 닉네임 업데이트
     */
    public int updateNickname(String userId, String newNickname) {
        String sql = "UPDATE member SET nickname=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newNickname);
            pstmt.setString(2, userId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 이메일 업데이트
     */
    public int updateEmail(String userId, String newEmail) {
        String sql = "UPDATE member SET email=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newEmail);
            pstmt.setString(2, userId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
    
    /**
     * 비밀번호 업데이트
     */
    public int updatePassword(String userId, String newPassword) {
        String sql = "UPDATE member SET pass=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection(); PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newPassword);
            pstmt.setString(2, userId);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 회원 탈퇴
     */
    public int deleteMember(String id) {
        String sql = "DELETE FROM member WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, id);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
} 