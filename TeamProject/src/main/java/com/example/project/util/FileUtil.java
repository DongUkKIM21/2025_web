package com.example.project.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.UUID;

import com.example.project.dto.FileDTO;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.Part;

public class FileUtil {
    // 파일 업로드 디렉토리 (웹 애플리케이션 루트 기준)
    private static final String UPLOAD_DIR = "Uploads";

    /**
     * 파일 업로드 처리
     */
    public static List<FileDTO> uploadFiles(HttpServletRequest req) throws IOException, ServletException {
        List<FileDTO> fileList = new ArrayList<>();
        String uploadPath = req.getServletContext().getRealPath("/") + UPLOAD_DIR;
        
        File uploadDir = new File(uploadPath);
        if (!uploadDir.exists()) {
            uploadDir.mkdirs();
        }

        Collection<Part> parts = req.getParts();
        for (Part part : parts) {
            if (part.getSubmittedFileName() != null && !part.getSubmittedFileName().isEmpty()) {
                String originalFileName = part.getSubmittedFileName();
                String storedFileName = createStoredFileName(originalFileName);

                part.write(uploadPath + File.separator + storedFileName);

                FileDTO fileDto = new FileDTO();
                fileDto.setOriginal_file_name(originalFileName);
                fileDto.setStored_file_name(storedFileName);
                fileDto.setFile_size(part.getSize());
                fileList.add(fileDto);
            }
        }
        return fileList;
    }

    /**
     * 파일 다운로드 처리
     */
    public static void download(HttpServletRequest req, HttpServletResponse resp, String storedFileName, String originalFileName) throws IOException {
        String uploadPath = req.getServletContext().getRealPath("/") + UPLOAD_DIR;
        File file = new File(uploadPath, storedFileName);

        if (file.exists()) {
            // 원본 파일 이름으로 인코딩 설정
            originalFileName = URLEncoder.encode(originalFileName, "UTF-8").replaceAll("\\+", "%20");
            
            resp.setContentType("application/octet-stream");
            resp.setContentLength((int)file.length());
            resp.setHeader("Content-Disposition", "attachment; filename=\"" + originalFileName + "\"");

            try (InputStream in = new FileInputStream(file);
                 OutputStream out = resp.getOutputStream()) {
                byte[] buffer = new byte[4096];
                int bytesRead = -1;
                while ((bytesRead = in.read(buffer)) != -1) {
                    out.write(buffer, 0, bytesRead);
                }
            }
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "File not found");
        }
    }

    /**
     * 서버에 저장된 파일 삭제
     */
    public static void deleteFile(HttpServletRequest req, String storedFileName) throws IOException {
        String uploadPath = req.getServletContext().getRealPath("/") + UPLOAD_DIR;
        try {
            Files.deleteIfExists(Paths.get(uploadPath, storedFileName));
        } catch (IOException e) {
            System.err.println("File deletion failed: " + e.getMessage());
            // 예외를 다시 던져서 호출 측에서 트랜잭션 롤백 등을 처리하도록 할 수 있음
            throw e; 
        }
    }

    /**
     * 저장될 파일명 생성 (중복 방지)
     */
    private static String createStoredFileName(String originalFileName) {
        // UUID + 타임스탬프 + 확장자
        String extension = "";
        int dotIndex = originalFileName.lastIndexOf('.');
        if (dotIndex >= 0) {
            extension = originalFileName.substring(dotIndex);
        }
        return UUID.randomUUID().toString() + "_" + new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date()) + extension;
    }
} 