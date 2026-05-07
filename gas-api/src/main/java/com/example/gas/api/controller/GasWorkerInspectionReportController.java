package com.example.gas.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.hutool.core.date.DateUtil;
import com.example.gas.api.common.R;
import com.example.gas.api.service.GasWorkerInspectionReportService;
import io.minio.MinioClient;
import io.minio.GetObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.io.InputStream;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/inspection_report")
@Slf4j
public class GasWorkerInspectionReportController {

    @Resource
    private GasWorkerInspectionReportService gasWorkerInspectionReportService;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @PostMapping("/export")
    @SaCheckLogin
    public ResponseEntity<byte[]> exportInspectionReport(@RequestBody(required = false) Map<String, Object> param) {
        try {
            Map<String, Object> queryParam = new HashMap<>();
            
            if (param != null) {
                if (param.containsKey("workerName")) {
                    queryParam.put("workerName", param.get("workerName"));
                }
                if (param.containsKey("deptId")) {
                    queryParam.put("deptId", param.get("deptId"));
                }
                if (param.containsKey("startDate")) {
                    queryParam.put("startDate", param.get("startDate"));
                }
                if (param.containsKey("endDate")) {
                    queryParam.put("endDate", param.get("endDate"));
                }
                if (param.containsKey("hasDanger")) {
                    queryParam.put("hasDanger", param.get("hasDanger"));
                }
                if (param.containsKey("inspectResult")) {
                    queryParam.put("inspectResult", param.get("inspectResult"));
                }
            }
            
            byte[] docxContent = gasWorkerInspectionReportService.generateInspectionReport(queryParam);
            
            String fileName = "燃气巡检报告_" + DateUtil.format(DateUtil.date(), "yyyyMMddHHmmss") + ".docx";
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", encodedFileName);
            headers.set("Access-Control-Expose-Headers", "Content-Disposition");
            
            log.info("巡检报告导出成功，文件名：{}", fileName);
            return new ResponseEntity<>(docxContent, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("导出巡检报告失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/exportByPlanId")
    @SaCheckLogin
    public ResponseEntity<byte[]> exportInspectionReportByPlanId(@RequestParam Integer planId) {
        try {
            Map<String, Object> queryParam = new HashMap<>();
            queryParam.put("planId", planId);
            
            byte[] docxContent = gasWorkerInspectionReportService.generateInspectionReportByPlanId(planId);
            
            String fileName = "燃气巡检报告_" + DateUtil.format(DateUtil.date(), "yyyyMMddHHmmss") + ".docx";
            String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                    .replaceAll("\\+", "%20");
            
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
            headers.setContentDispositionFormData("attachment", encodedFileName);
            headers.set("Access-Control-Expose-Headers", "Content-Disposition");
            
            log.info("巡检报告导出成功，planId：{}，文件名：{}", planId, fileName);
            return new ResponseEntity<>(docxContent, headers, HttpStatus.OK);
            
        } catch (Exception e) {
            log.error("导出巡检报告失败，planId：{}", planId, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    @PostMapping("/generateAndUpload")
    @SaCheckLogin
    public R generateAndUploadReport(@RequestParam Integer planId) {
        try {
            String filePath = gasWorkerInspectionReportService.generateAndUploadReport(planId);
            Map<String, Object> result = new HashMap<>();
            result.put("filePath", filePath);
            result.put("fileName", "燃气巡检报告_" + planId + ".docx");
            return R.ok().put("result", result);
        } catch (Exception e) {
            log.error("生成并上传巡检报告失败，planId：{}", planId, e);
            return R.error("生成报告失败: " + e.getMessage());
        }
    }

    @GetMapping("/download")
    @SaCheckLogin
    public ResponseEntity<byte[]> downloadReport(@RequestParam String filePath, @RequestParam(required = false) String fileName) {
        try {
            MinioClient client = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            try (InputStream inputStream = client.getObject(
                    GetObjectArgs.builder()
                            .bucket("report")
                            .object(filePath)
                            .build()
            )) {
                byte[] content = inputStream.readAllBytes();
                
                if (fileName == null || fileName.isEmpty()) {
                    fileName = "巡检报告.docx";
                }
                String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                        .replaceAll("\\+", "%20");
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.APPLICATION_OCTET_STREAM);
                headers.setContentDispositionFormData("attachment", encodedFileName);
                headers.set("Access-Control-Expose-Headers", "Content-Disposition");
                
                log.info("巡检报告下载成功，文件路径：{}", filePath);
                return new ResponseEntity<>(content, headers, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error("下载巡检报告失败，文件路径：{}", filePath, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
