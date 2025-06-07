package com.example.project.dto;

import java.sql.Timestamp;

public class MemberDTO {
    private String id;
    private String pass;
    private String name;
    private String nickname;
    private String email;
    private Timestamp regidate;
    private int admin; // 0: 일반사용자, 1: 관리자

    // 기본 생성자
    public MemberDTO() {}

    // 모든 필드를 받는 생성자 (오버로딩)
    public MemberDTO(String id, String pass, String name, String nickname, String email) {
        this.id = id;
        this.pass = pass;
        this.name = name;
        this.nickname = nickname;
        this.email = email;
    }

    // Getters and Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPass() {
        return pass;
    }

    public void setPass(String pass) {
        this.pass = pass;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNickname() {
        return nickname;
    }

    public void setNickname(String nickname) {
        this.nickname = nickname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public Timestamp getRegidate() {
        return regidate;
    }

    public void setRegidate(Timestamp regidate) {
        this.regidate = regidate;
    }

    public int getAdmin() {
        return admin;
    }

    public void setAdmin(int admin) {
        this.admin = admin;
    }
} 