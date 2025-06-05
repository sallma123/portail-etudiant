// FileController.java
package com.etudiant.gestion_etudiant.controller;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.io.FileSystemResource;
import org.springframework.core.io.Resource;
import org.springframework.http.*;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.io.File;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;

@Controller
@RequestMapping("/fichiers")
public class FileController {

    private final String uploadDir = "uploads";

    @GetMapping("/{fileName:.+}")
    public ResponseEntity<Resource> getFile(@PathVariable String fileName,
                                            @RequestParam(defaultValue = "inline") String disposition,
                                            HttpServletResponse response) {
        try {
            File file = new File(uploadDir, fileName);
            if (!file.exists()) {
                return ResponseEntity.notFound().build();
            }

            FileSystemResource resource = new FileSystemResource(file);
            String encodedFileName = URLEncoder.encode(file.getName(), StandardCharsets.UTF_8);
            String headerValue = disposition + "; filename=\"" + encodedFileName + "\"";

            HttpHeaders headers = new HttpHeaders();
            headers.add(HttpHeaders.CONTENT_DISPOSITION, headerValue);
            headers.add(HttpHeaders.CONTENT_TYPE, Files.probeContentType(file.toPath()));

            return ResponseEntity.ok()
                    .headers(headers)
                    .body(resource);

        } catch (Exception e) {
            return ResponseEntity.internalServerError().build();
        }
    }
}
