package com.example.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.project.dto.BoardDTO;
import com.example.project.dto.FileDTO;

public class BoardDAO {

    /**
     * 게시물 목록 조회 (검색 및 페이징 포함)
     */
    public List<BoardDTO> selectListPage(Map<String, Object> map) {
        List<BoardDTO> boardList = new ArrayList<>();
        String sql = "SELECT * FROM board ";

        if (map.get("searchWord") != null && !map.get("searchWord").toString().isEmpty()) {
            sql += " WHERE " + map.get("searchField") + " LIKE ? ";
        }

        sql += " ORDER BY num DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (map.get("searchWord") != null && !map.get("searchWord").toString().isEmpty()) {
                pstmt.setString(1, "%" + map.get("searchWord") + "%");
                pstmt.setInt(2, (int)map.get("offset"));
                pstmt.setInt(3, (int)map.get("pageSize"));
            } else {
                pstmt.setInt(1, (int)map.get("offset"));
                pstmt.setInt(2, (int)map.get("pageSize"));
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    BoardDTO dto = new BoardDTO();
                    dto.setNum(rs.getInt("num"));
                    dto.setTitle(rs.getString("title"));
                    dto.setContent(rs.getString("content"));
                    dto.setId(rs.getString("id"));
                    dto.setPostdate(rs.getTimestamp("postdate"));
                    dto.setVisitcount(rs.getInt("visitcount"));
                    boardList.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return boardList;
    }

    /**
     * 전체 게시물 수 조회 (검색 조건 포함)
     */
    public int selectCount(Map<String, Object> map) {
        int totalCount = 0;
        String sql = "SELECT COUNT(*) FROM board";
        if (map.get("searchWord") != null && !map.get("searchWord").toString().isEmpty()) {
            sql += " WHERE " + map.get("searchField") + " LIKE ? ";
        }

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
            if (map.get("searchWord") != null && !map.get("searchWord").toString().isEmpty()) {
                pstmt.setString(1, "%" + map.get("searchWord") + "%");
            }

            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    totalCount = rs.getInt(1);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return totalCount;
    }

    /**
     * 게시물 작성
     */
    public int insertWrite(BoardDTO dto) {
        String sql = "INSERT INTO board (title, content, id) VALUES (?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            pstmt.setString(1, dto.getTitle());
            pstmt.setString(2, dto.getContent());
            pstmt.setString(3, dto.getId());
            
            int affectedRows = pstmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = pstmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        return rs.getInt(1); // Return generated post number
                    }
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 첨부파일 추가
     */
    public void insertFile(FileDTO dto) {
        String sql = "INSERT INTO file_board (board_num, original_file_name, stored_file_name, file_size) VALUES (?, ?, ?, ?)";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, dto.getBoard_num());
            pstmt.setString(2, dto.getOriginal_file_name());
            pstmt.setString(3, dto.getStored_file_name());
            pstmt.setLong(4, dto.getFile_size());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 게시물 상세 보기
     */
    public BoardDTO selectView(String num) {
        String sql = "SELECT * FROM board WHERE num=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(num));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    BoardDTO dto = new BoardDTO();
                    dto.setNum(rs.getInt("num"));
                    dto.setTitle(rs.getString("title"));
                    dto.setContent(rs.getString("content"));
                    dto.setId(rs.getString("id"));
                    dto.setPostdate(rs.getTimestamp("postdate"));
                    dto.setVisitcount(rs.getInt("visitcount"));
                    return dto;
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 게시물 조회수 1 증가
     */
    public void updateVisitCount(String num) {
        String sql = "UPDATE board SET visitcount = visitcount + 1 WHERE num=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(num));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 해당 게시물의 첨부파일 목록 조회
     */
    public List<FileDTO> selectFiles(String boardNum) {
        List<FileDTO> fileList = new ArrayList<>();
        String sql = "SELECT * FROM file_board WHERE board_num=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(boardNum));
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    FileDTO dto = new FileDTO();
                    dto.setIdx(rs.getInt("idx"));
                    dto.setBoard_num(rs.getInt("board_num"));
                    dto.setOriginal_file_name(rs.getString("original_file_name"));
                    dto.setStored_file_name(rs.getString("stored_file_name"));
                    dto.setFile_size(rs.getLong("file_size"));
                    fileList.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fileList;
    }

    /**
     * 게시물 수정
     */
    public int updatePost(BoardDTO dto) {
        String sql = "UPDATE board SET title=?, content=? WHERE num=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, dto.getTitle());
            pstmt.setString(2, dto.getContent());
            pstmt.setInt(3, dto.getNum());
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 게시물 삭제
     */
    public int deletePost(String num) {
        String sql = "DELETE FROM board WHERE num=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(num));
            return pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 특정 첨부파일 삭제
     */
    public void deleteFile(String idx) {
        String sql = "DELETE FROM file_board WHERE idx=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(idx));
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
} 