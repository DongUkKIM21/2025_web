package com.example.project.util;

import com.example.project.dto.FileDTO;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;

public class FileUtil {

    private static final String UPLOAD_DIR = "uploads";

    /**
     * 서블릿 내장 API를 사용하여 파일 업로드를 처리합니다.
     * @param req HttpServletRequest 객체
     * @return 업로드된 파일 정보 목록
     */
    public static List<FileDTO> uploadFiles(HttpServletRequest req) throws IOException, ServletException {
        List<FileDTO> fileList = new ArrayList<>();
        
        String uploadPath = req.getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdir();
        }

        Collection<Part> parts = req.getParts();
        for (Part part : parts) {
            String fileName = part.getSubmittedFileName();

            if (fileName != null && !fileName.isEmpty()) {
                String ext = fileName.substring(fileName.lastIndexOf("."));
                String storedFileName = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + "_" + System.currentTimeMillis() + ext;
                
                part.write(uploadPath + File.separator + storedFileName);

                FileDTO fileDto = new FileDTO();
                fileDto.setOriginal_file_name(fileName);
                fileDto.setStored_file_name(storedFileName);
                fileDto.setFile_size(part.getSize());
                fileList.add(fileDto);
            }
        }
        return fileList;
    }

    /**
     * 지정된 파일을 찾아 다운로드합니다.
     */
    public static void download(HttpServletRequest req, HttpServletResponse resp,
            String storedFileName, String originalFileName) {
        String uploadPath = req.getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;

        try {
            File file = new File(uploadPath, storedFileName);
            InputStream in = new FileInputStream(file);

            resp.reset();
            resp.setContentType("application/octet-stream");
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + new String(originalFileName.getBytes("UTF-8"), "ISO-8859-1") + "\"");
            resp.setHeader("Content-Length", "" + file.length());

            OutputStream out = resp.getOutputStream();
            byte[] buffer = new byte[4096];
            int bytesRead = -1;
            while ((bytesRead = in.read(buffer)) != -1) {
                out.write(buffer, 0, bytesRead);
            }
            in.close();
            out.close();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * 디스크에서 파일을 삭제합니다.
     */
    public static void deleteFile(HttpServletRequest req, String filename) {
        String uploadPath = req.getServletContext().getRealPath("") + File.separator + UPLOAD_DIR;
        File file = new File(uploadPath + File.separator + filename);
        if (file.exists()) {
            file.delete();
        }
    }
} 