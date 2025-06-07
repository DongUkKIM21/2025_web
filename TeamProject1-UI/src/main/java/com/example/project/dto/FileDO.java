package com.example.project.dto;

public class FileDO {
    private int idx;
    private int board_num; // 게시물 번호
    private String original_file_name;
    private String stored_file_name;
    private long file_size;

    // Getters and Setters
    public int getIdx() {
        return idx;
    }

    public void setIdx(int idx) {
        this.idx = idx;
    }

    public int getBoard_num() {
        return board_num;
    }

    public void setBoard_num(int board_num) {
        this.board_num = board_num;
    }

    public String getOriginal_file_name() {
        return original_file_name;
    }

    public void setOriginal_file_name(String original_file_name) {
        this.original_file_name = original_file_name;
    }

    public String getStored_file_name() {
        return stored_file_name;
    }

    public void setStored_file_name(String stored_file_name) {
        this.stored_file_name = stored_file_name;
    }

    public long getFile_size() {
        return file_size;
    }

    public void setFile_size(long file_size) {
        this.file_size = file_size;
    }
} 