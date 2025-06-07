package com.example.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import com.example.project.dto.CommentDO;
import com.example.project.dao.DBUtil;

public class CommentDAO {

    public List<CommentDO> getComments(int bno) {
        List<CommentDO> comments = new ArrayList<>();
        String sql = "SELECT * FROM comment WHERE bno = ? ORDER BY cno ASC";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, bno);
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    CommentDO dto = new CommentDO();
                    dto.setCno(rs.getInt("cno"));
                    dto.setBno(rs.getInt("bno"));
                    dto.setId(rs.getString("id"));
                    dto.setNickname(rs.getString("nickname"));
                    dto.setContent(rs.getString("content"));
                    dto.setPostdate(rs.getTimestamp("postdate"));
                    comments.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return comments;
    }

    public int addComment(CommentDO dto) {
        String sql = "INSERT INTO comment (bno, id, nickname, content) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, dto.getBno());
            pstmt.setString(2, dto.getId());
            pstmt.setString(3, dto.getNickname());
            pstmt.setString(4, dto.getContent());
            
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public int deleteComment(int cno) {
        String sql = "DELETE FROM comment WHERE cno = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            pstmt.setInt(1, cno);
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }
} 