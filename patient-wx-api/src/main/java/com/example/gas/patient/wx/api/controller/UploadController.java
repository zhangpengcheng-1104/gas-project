package com.example.gas.patient.wx.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import com.example.gas.patient.wx.api.common.R;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/upload")
public class UploadController {

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @PostMapping("/inspectionPhoto")
    @SaCheckLogin
    public R uploadInspectionPhoto(@RequestParam("file") MultipartFile file,
                                    @RequestParam("workerPlanId") Integer workerPlanId) {
        if (file.isEmpty()) {
            return R.error().put("msg", "文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));

        if (!suffix.equalsIgnoreCase(".jpg") && !suffix.equalsIgnoreCase(".jpeg") && !suffix.equalsIgnoreCase(".png")) {
            return R.error().put("msg", "只支持JPG、JPEG、PNG格式的图片");
        }

        try {
            String filename = "insp-" + workerPlanId + "-" + System.currentTimeMillis() + suffix;

            MinioClient client = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            client.putObject(
                    PutObjectArgs.builder()
                            .bucket("inspection")
                            .object(filename)
                            .stream(file.getInputStream(), -1, 5 * 1024 * 1024)
                            .contentType(file.getContentType())
                            .build()
            );

            String photoPath = "/" + filename;
            Map<String, Object> result = new HashMap<>();
            result.put("photo", photoPath);
            result.put("url", photoPath);
            result.put("path", photoPath);

            return R.ok().put("result", result);
        } catch (Exception e) {
            e.printStackTrace();
            return R.error().put("msg", "文件上传失败: " + e.getMessage());
        }
    }
}
