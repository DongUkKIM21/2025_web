package com.example.project.dto;

import java.sql.Timestamp;

public class CommentDO {
    private int cno;
    private int bno;
    private String id;
    private String nickname;
    private String content;
    private Timestamp postdate;
    private String board_title;

    // Getters and Setters
    public String getBoard_title() {
        return board_title;
    }
    public void setBoard_title(String board_title) {
        this.board_title = board_title;
    }
    public int getCno() {
        return cno;
    }
    public void setCno(int cno) {
        this.cno = cno;
    }
    public int getBno() {
        return bno;
    }
    public void setBno(int bno) {
        this.bno = bno;
    }
    public String getId() {
        return id;
    }
    public void setId(String id) {
        this.id = id;
    }
    public String getNickname() {
        return nickname;
    }
    public void setNickname(String nickname) {
        this.nickname = nickname;
    }
    public String getContent() {
        return content;
    }
    public void setContent(String content) {
        this.content = content;
    }
    public Timestamp getPostdate() {
        return postdate;
    }
    public void setPostdate(Timestamp postdate) {
        this.postdate = postdate;
    }
} 