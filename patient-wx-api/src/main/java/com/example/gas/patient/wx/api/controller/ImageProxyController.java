package com.example.gas.patient.wx.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.gas.patient.wx.api.common.R;
import io.minio.MinioClient;
import io.minio.GetObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.InputStream;

@RestController
@RequestMapping("/image")
public class ImageProxyController {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @GetMapping("/inspection")
    @SaCheckLogin
    public ResponseEntity<byte[]> proxyInspectionImage(@RequestParam String path) {
        try {
            String cleanPath = path;
            if (cleanPath.startsWith("/")) {
                cleanPath = cleanPath.substring(1);
            }

            MinioClient client = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            InputStream inputStream = client.getObject(
                    GetObjectArgs.builder()
                            .bucket("inspection")
                            .object(cleanPath)
                            .build()
            );

            byte[] bytes = inputStream.readAllBytes();
            inputStream.close();

            MediaType mediaType = MediaType.IMAGE_JPEG;
            if (cleanPath.toLowerCase().endsWith(".png")) {
                mediaType = MediaType.IMAGE_PNG;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setContentLength(bytes.length);
            headers.setCacheControl("max-age=86400");

            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/worker")
    @SaCheckLogin
    public ResponseEntity<byte[]> proxyWorkerImage(@RequestParam String path) {
        try {
            String cleanPath = path;
            if (cleanPath.startsWith("/")) {
                cleanPath = cleanPath.substring(1);
            }

            MinioClient client = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            InputStream inputStream = client.getObject(
                    GetObjectArgs.builder()
                            .bucket("worker")
                            .object(cleanPath)
                            .build()
            );

            byte[] bytes = inputStream.readAllBytes();
            inputStream.close();

            MediaType mediaType = MediaType.IMAGE_JPEG;
            if (cleanPath.toLowerCase().endsWith(".png")) {
                mediaType = MediaType.IMAGE_PNG;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setContentLength(bytes.length);
            headers.setCacheControl("max-age=86400");

            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    @GetMapping("/rectification")
    @SaCheckLogin
    public ResponseEntity<byte[]> proxyRectificationFile(@RequestParam String path) {
        try {
            String cleanPath = path;
            if (cleanPath.startsWith("/")) {
                cleanPath = cleanPath.substring(1);
            }

            MinioClient client = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            InputStream inputStream = client.getObject(
                    GetObjectArgs.builder()
                            .bucket("rectification")
                            .object(cleanPath)
                            .build()
            );

            byte[] bytes = inputStream.readAllBytes();
            inputStream.close();

            MediaType mediaType = MediaType.IMAGE_JPEG;
            String lowerPath = cleanPath.toLowerCase();
            if (lowerPath.endsWith(".png")) {
                mediaType = MediaType.IMAGE_PNG;
            } else if (lowerPath.endsWith(".mp4") || lowerPath.endsWith(".mov") || lowerPath.endsWith(".avi")) {
                mediaType = MediaType.APPLICATION_OCTET_STREAM;
            }

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setContentLength(bytes.length);
            headers.setCacheControl("max-age=86400");

            return new ResponseEntity<>(bytes, headers, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
}
