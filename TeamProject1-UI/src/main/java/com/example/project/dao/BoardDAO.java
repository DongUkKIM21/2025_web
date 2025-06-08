package com.example.project.dao;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.example.project.dto.BoardDO;
import com.example.project.dto.FileDO;

public class BoardDAO {

    /**
     * 게시물 목록 조회 (검색 및 페이징 포함)
     */
    public List<BoardDO> selectListPage(Map<String, Object> map) {
        List<BoardDO> bbs = new ArrayList<>();
        StringBuilder query = new StringBuilder("SELECT b.*, m.nickname FROM board b JOIN member m ON b.id = m.id");
        List<Object> params = new ArrayList<>();
        
        buildWhereClause(map, query, params);

        query.append(" ORDER BY b.num DESC OFFSET ? ROWS FETCH NEXT ? ROWS ONLY");
        params.add(map.get("offset"));
        params.add(map.get("pageSize"));

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement psmt = conn.prepareStatement(query.toString())) {

            for (int i = 0; i < params.size(); i++) {
                psmt.setObject(i + 1, params.get(i));
            }

            ResultSet rs = psmt.executeQuery();
            while (rs.next()) {
                BoardDO dto = new BoardDO();
                dto.setNum(rs.getInt("num"));
                dto.setTitle(rs.getString("title"));
                dto.setContent(rs.getString("content"));
                dto.setPostdate(rs.getTimestamp("postdate"));
                dto.setId(rs.getString("id"));
                dto.setNickname(rs.getString("nickname"));
                dto.setVisitcount(rs.getInt("visitcount"));
                dto.setLike_count(rs.getInt("like_count"));
                dto.setCategory(rs.getString("category"));
                bbs.add(dto);
            }
        } catch (Exception e) {
            System.out.println("게시물 조회 중 예외 발생");
            e.printStackTrace();
        }
        return bbs;
    }

    /**
     * 전체 게시물 수 조회 (검색 조건 포함)
     */
    public int selectCount(Map<String, Object> map) {
        int totalCount = 0;
        StringBuilder query = new StringBuilder("SELECT COUNT(*) FROM board");
        List<Object> params = new ArrayList<>();

        buildWhereClause(map, query, params);

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement psmt = conn.prepareStatement(query.toString())) {
            
            for (int i = 0; i < params.size(); i++) {
                psmt.setObject(i + 1, params.get(i));
            }

            try (ResultSet rs = psmt.executeQuery()) {
                if (rs.next()) {
                    totalCount = rs.getInt(1);
                }
            }
        } catch (Exception e) {
            System.out.println("게시물 카운트 중 예외 발생");
            e.printStackTrace();
        }
        return totalCount;
    }

    private void buildWhereClause(Map<String, Object> map, StringBuilder query, List<Object> params) {
        boolean hasWhere = false;

        if (map.get("category") != null && !map.get("category").toString().trim().isEmpty()) {
            query.append(" WHERE category = ?");
            params.add(map.get("category"));
            hasWhere = true;
        }

        if (map.get("searchWord") != null && !map.get("searchWord").toString().trim().isEmpty()) {
            if (hasWhere) {
                query.append(" AND");
            } else {
                query.append(" WHERE");
            }
            query.append(" ").append(map.get("searchField")).append(" LIKE ?");
            params.add("%" + map.get("searchWord") + "%");
        }
    }

    /**
     * 게시물 작성
     */
    public int insertWrite(BoardDO dto) {
        int newPostNum = 0;
        String query = "INSERT INTO board (num, title, content, id, category) VALUES (NEXT VALUE FOR seq_board_num, ?, ?, ?, ?)";
        String[] generatedColumns = {"num"};

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement psmt = conn.prepareStatement(query, generatedColumns)) {
            psmt.setString(1, dto.getTitle());
            psmt.setString(2, dto.getContent());
            psmt.setString(3, dto.getId());
            psmt.setString(4, dto.getCategory());
            
            int affectedRows = psmt.executeUpdate();

            if (affectedRows > 0) {
                try (ResultSet rs = psmt.getGeneratedKeys()) {
                    if (rs.next()) {
                        newPostNum = rs.getInt(1);
                    }
                }
            }
        } catch (Exception e) {
            System.out.println("게시물 입력 중 예외 발생");
            e.printStackTrace();
        }
        return newPostNum;
    }

    /**
     * 첨부파일 추가
     */
    public void insertFile(FileDO dto) {
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
    public BoardDO selectView(String num) {
        BoardDO dto = new BoardDO();
        String query = "SELECT b.*, m.nickname FROM board b JOIN member m ON b.id = m.id WHERE num = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement psmt = conn.prepareStatement(query)) {
            psmt.setString(1, num);
            ResultSet rs = psmt.executeQuery();
            if (rs.next()) {
                dto.setNum(rs.getInt("num"));
                dto.setTitle(rs.getString("title"));
                dto.setContent(rs.getString("content"));
                dto.setPostdate(rs.getTimestamp("postdate"));
                dto.setId(rs.getString("id"));
                dto.setNickname(rs.getString("nickname"));
                dto.setVisitcount(rs.getInt("visitcount"));
                dto.setLike_count(rs.getInt("like_count"));
                dto.setCategory(rs.getString("category"));
            }
        } catch (Exception e) {
            System.out.println("게시물 상세보기 중 예외 발생");
            e.printStackTrace();
        }
        return dto;
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
    public List<FileDO> selectFiles(String boardNum) {
        List<FileDO> fileList = new ArrayList<>();
        String sql = "SELECT * FROM file_board WHERE board_num=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(boardNum));
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    FileDO dto = new FileDO();
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
    public int updatePost(BoardDO dto) {
        int result = 0;
        String query = "UPDATE board SET title=?, content=?, category=? WHERE num=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement psmt = conn.prepareStatement(query)) {
            psmt.setString(1, dto.getTitle());
            psmt.setString(2, dto.getContent());
            psmt.setString(3, dto.getCategory());
            psmt.setInt(4, dto.getNum());
            result = psmt.executeUpdate();
        } catch (Exception e) {
            System.out.println("게시물 수정 중 예외 발생");
            e.printStackTrace();
        }
        return result;
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
     * 관리자에 의한 회원 ID 변경 시 게시물의 회원 ID 업데이트
     */
    public int updateMemberIdInBoard(String oldId, String newId) {
        String sql = "UPDATE board SET id=? WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, newId);
            pstmt.setString(2, oldId);
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

    /**
     * 특정 회원의 모든 게시물 삭제
     */
    public void deletePostsByMemberId(String memberId) {
        String sql = "DELETE FROM board WHERE id=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, memberId);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    /**
     * 게시물 추천 처리 (트랜잭션 관리)
     * @return 1: 추천 성공, 2: 이미 추천함, 0: 실패
     */
    public int processLike(String boardNum, String userId) {
        String checkSql = "SELECT COUNT(*) FROM board_likes WHERE board_num = ? AND user_id = ?";
        String insertSql = "INSERT INTO board_likes (board_num, user_id) VALUES (?, ?)";
        String updateSql = "UPDATE board SET like_count = like_count + 1 WHERE num = ?";
        
        Connection conn = null;
        try {
            conn = DBUtil.getConnection();
            conn.setAutoCommit(false); // 트랜잭션 시작

            // System.out.println("--- 추천 처리 시작 ---");
            // System.out.println("게시글 번호: " + boardNum + ", 사용자 ID: " + userId);

            // 이미 추천했는지 확인
            try (PreparedStatement checkPstmt = conn.prepareStatement(checkSql)) {
                checkPstmt.setInt(1, Integer.parseInt(boardNum));
                checkPstmt.setString(2, userId);
                try (ResultSet rs = checkPstmt.executeQuery()) {
                    if (rs.next() && rs.getInt(1) > 0) {
                        // System.out.println("추천 확인 결과 (count): " + rs.getInt(1));
                        // System.out.println("이미 추천한 게시물입니다. 롤백 후 2를 반환합니다.");
                        conn.rollback();
                        return 2; // 이미 추천함
                    }
                }
            }

            // board_likes 테이블에 추천 기록 추가
            try (PreparedStatement insertPstmt = conn.prepareStatement(insertSql)) {
                insertPstmt.setInt(1, Integer.parseInt(boardNum));
                insertPstmt.setString(2, userId);
                insertPstmt.executeUpdate();
            }

            // board 테이블의 like_count 업데이트
            try (PreparedStatement updatePstmt = conn.prepareStatement(updateSql)) {
                updatePstmt.setInt(1, Integer.parseInt(boardNum));
                updatePstmt.executeUpdate();
            }
            
            conn.commit(); // 트랜잭션 커밋
            // System.out.println("추천 처리 성공. 커밋 완료.");
            return 1; // 성공

        } catch (SQLException e) {
            e.printStackTrace();
            if (conn != null) {
                try {
                    // System.out.println("오류 발생. 롤백합니다.");
                    conn.rollback();
                } catch (SQLException ex) {
                    ex.printStackTrace();
                }
            }
            return 0; // 실패
        } finally {
            if (conn != null) {
                try {
                    conn.setAutoCommit(true);
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * 게시물 추천수 조회
     */
    public int getLikeCount(String num) {
        int likeCount = 0;
        String sql = "SELECT like_count FROM board WHERE num=?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(num));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    likeCount = rs.getInt("like_count");
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return likeCount;
    }

    public FileDO selectFile(String fileIdx) {
        FileDO fileDto = null;
        String sql = "SELECT * FROM file_board WHERE idx = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setInt(1, Integer.parseInt(fileIdx));
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    fileDto = new FileDO();
                    fileDto.setIdx(rs.getInt("idx"));
                    fileDto.setBoard_num(rs.getInt("board_num"));
                    fileDto.setOriginal_file_name(rs.getString("original_file_name"));
                    fileDto.setStored_file_name(rs.getString("stored_file_name"));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return fileDto;
    }

    /**
     * 관리자용: 모든 게시물 목록 조회
     */
    public List<BoardDO> selectAllPostsForAdmin() {
        List<BoardDO> bbs = new ArrayList<>();
        String query = "SELECT b.*, m.nickname FROM board b JOIN member m ON b.id = m.id ORDER BY b.num DESC";

        try (Connection conn = DBUtil.getConnection();
             PreparedStatement psmt = conn.prepareStatement(query);
             ResultSet rs = psmt.executeQuery()) {

            while (rs.next()) {
                BoardDO dto = new BoardDO();
                dto.setNum(rs.getInt("num"));
                dto.setTitle(rs.getString("title"));
                dto.setContent(rs.getString("content"));
                dto.setPostdate(rs.getTimestamp("postdate"));
                dto.setId(rs.getString("id"));
                dto.setNickname(rs.getString("nickname"));
                dto.setVisitcount(rs.getInt("visitcount"));
                dto.setLike_count(rs.getInt("like_count"));
                dto.setCategory(rs.getString("category"));
                bbs.add(dto);
            }
        } catch (Exception e) {
            System.out.println("관리자 게시물 조회 중 예외 발생");
            e.printStackTrace();
        }
        return bbs;
    }

    /**
     * 특정 회원이 작성한 모든 게시물 목록 조회
     */
    public List<BoardDO> selectPostsByMemberId(String memberId) {
        List<BoardDO> postList = new ArrayList<>();
        String sql = "SELECT * FROM board WHERE id = ?";
        try (Connection conn = DBUtil.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setString(1, memberId);
            try (ResultSet rs = pstmt.executeQuery()) {
                while(rs.next()) {
                    BoardDO dto = new BoardDO();
                    dto.setNum(rs.getInt("num"));
                    dto.setTitle(rs.getString("title"));
                    dto.setId(rs.getString("id"));
                    // 필요한 다른 필드들도 여기서 채울 수 있습니다.
                    postList.add(dto);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return postList;
    }
} 