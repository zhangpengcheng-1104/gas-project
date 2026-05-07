package com.example.gas.api.controller;

import cn.dev33.satoken.annotation.SaCheckLogin;
import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.annotation.SaMode;
import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.date.DateUtil;
import com.example.gas.api.common.PageUtils;
import com.example.gas.api.common.R;
import com.example.gas.api.db.pojo.GasHazardInfoEntity;
import com.example.gas.api.service.GasHazardInfoService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.GetObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.io.InputStream;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.*;

@RestController
@RequestMapping("/hazard_info")
@Slf4j
public class GasHazardInfoController {

    @Resource
    private GasHazardInfoService gasHazardInfoService;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @PostMapping("/searchByPage")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "HAZARD_INFO:SELECT"}, mode = SaMode.OR)
    public R searchByPage(@RequestBody Map<String, Object> params) {
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int length = params.get("length") != null ? Integer.parseInt(params.get("length").toString()) : 10;
        int start = (page - 1) * length;
        params.put("start", start);
        PageUtils pageUtils = gasHazardInfoService.searchByPage(params);
        return R.ok().put("result", pageUtils);
    }

    @PostMapping("/searchById")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "HAZARD_INFO:SELECT"}, mode = SaMode.OR)
    public R searchById(@RequestBody Map<String, Object> params) {
        Integer id = Integer.parseInt(params.get("id").toString());
        HashMap map = gasHazardInfoService.searchByIdHashMap(id);
        return R.ok(map);
    }

    @PostMapping("/save")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "HAZARD_INFO:INSERT"}, mode = SaMode.OR)
    public R save(@RequestBody Map<String, Object> params) {
        GasHazardInfoEntity entity = new GasHazardInfoEntity();
        BeanUtil.copyProperties(params, entity);
        
        if (params.get("longitude") != null) {
            entity.setLongitude(new BigDecimal(params.get("longitude").toString()));
        }
        if (params.get("latitude") != null) {
            entity.setLatitude(new BigDecimal(params.get("latitude").toString()));
        }
        if (params.get("userId") != null && !params.get("userId").toString().isEmpty()) {
            entity.setUserId(Integer.parseInt(params.get("userId").toString()));
        }
        if (params.get("assigneeId") != null && !params.get("assigneeId").toString().isEmpty()) {
            entity.setAssigneeId(Integer.parseInt(params.get("assigneeId").toString()));
        }
        if (params.get("isSelfRectify") != null && !params.get("isSelfRectify").toString().isEmpty()) {
            entity.setIsSelfRectify(Integer.parseInt(params.get("isSelfRectify").toString()));
        }
        
        if (params.get("rectifyStartDate") != null && !params.get("rectifyStartDate").toString().isEmpty()) {
            entity.setRectifyStartDate(DateUtil.parseDate(params.get("rectifyStartDate").toString()));
        }
        if (params.get("rectifyEndDate") != null && !params.get("rectifyEndDate").toString().isEmpty()) {
            entity.setRectifyEndDate(DateUtil.parseDate(params.get("rectifyEndDate").toString()));
        }
        
        entity.setSubmitTime(new Date());
        
        gasHazardInfoService.insert(entity);
        return R.ok();
    }

    @PostMapping("/update")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "HAZARD_INFO:UPDATE"}, mode = SaMode.OR)
    public R update(@RequestBody Map<String, Object> params) {
        GasHazardInfoEntity entity = new GasHazardInfoEntity();
        BeanUtil.copyProperties(params, entity);
        
        if (params.get("longitude") != null) {
            entity.setLongitude(new BigDecimal(params.get("longitude").toString()));
        }
        if (params.get("latitude") != null) {
            entity.setLatitude(new BigDecimal(params.get("latitude").toString()));
        }
        if (params.get("userId") != null && !params.get("userId").toString().isEmpty()) {
            entity.setUserId(Integer.parseInt(params.get("userId").toString()));
        }
        if (params.get("assigneeId") != null && !params.get("assigneeId").toString().isEmpty()) {
            entity.setAssigneeId(Integer.parseInt(params.get("assigneeId").toString()));
        }
        if (params.get("isSelfRectify") != null && !params.get("isSelfRectify").toString().isEmpty()) {
            entity.setIsSelfRectify(Integer.parseInt(params.get("isSelfRectify").toString()));
        }
        
        if (params.get("rectifyStartDate") != null && !params.get("rectifyStartDate").toString().isEmpty()) {
            entity.setRectifyStartDate(DateUtil.parseDate(params.get("rectifyStartDate").toString()));
        }
        if (params.get("rectifyEndDate") != null && !params.get("rectifyEndDate").toString().isEmpty()) {
            entity.setRectifyEndDate(DateUtil.parseDate(params.get("rectifyEndDate").toString()));
        }
        
        gasHazardInfoService.update(entity);
        return R.ok();
    }

    @PostMapping("/delete")
    @SaCheckLogin
    @SaCheckPermission(value = {"ROOT", "HAZARD_INFO:DELETE"}, mode = SaMode.OR)
    public R delete(@RequestBody Map<String, Object> params) {
        Integer id = params.get("id") != null ? Integer.valueOf(params.get("id").toString()) : null;
        List<Integer> ids = (List<Integer>) params.get("ids");

        int result = 0;
        if (id != null) {
            result = gasHazardInfoService.deleteById(id);
        } else if (ids != null && !ids.isEmpty()) {
            result = gasHazardInfoService.deleteByIds(ids);
        }

        if (result > 0) {
            return R.ok();
        } else {
            return R.error();
        }
    }

    @PostMapping("/uploadPhoto")
    @SaCheckLogin
    public R uploadPhoto(@RequestParam("file") MultipartFile file,
                         @RequestParam(value = "hazardId", required = false) Integer hazardId) {
        if (file.isEmpty()) {
            return R.error().put("msg", "文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        
        if (!suffix.equalsIgnoreCase(".jpg") && !suffix.equalsIgnoreCase(".jpeg") && !suffix.equalsIgnoreCase(".png")) {
            return R.error().put("msg", "只支持JPG、JPEG、PNG格式的图片");
        }

        try {
            String filename = UUID.randomUUID().toString() + suffix;
            String objectName;
            if (hazardId != null) {
                objectName = "/photo/hazard/" + hazardId + "/" + filename;
            } else {
                objectName = "/photo/hazard/temp/" + filename;
            }

            MinioClient client = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            client.putObject(
                    PutObjectArgs.builder()
                            .bucket("hidden-danger")
                            .object(objectName)
                            .stream(file.getInputStream(), -1, 5 * 1024 * 1024)
                            .contentType(file.getContentType())
                            .build()
            );

            Map<String, Object> result = new HashMap<>();
            result.put("photo", objectName);
            result.put("url", objectName);
            result.put("path", objectName);
            
            return R.ok().put("result", result);
        } catch (Exception e) {
            log.error("上传隐患图片失败", e);
            return R.error().put("msg", "文件上传失败: " + e.getMessage());
        }
    }

    @GetMapping("/downloadPhoto")
    @SaCheckLogin
    public ResponseEntity<byte[]> downloadPhoto(@RequestParam String filePath) {
        try {
            MinioClient client = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            try (InputStream inputStream = client.getObject(
                    GetObjectArgs.builder()
                            .bucket("hidden-danger")
                            .object(filePath)
                            .build()
            )) {
                byte[] content = inputStream.readAllBytes();
                
                String fileName = filePath.substring(filePath.lastIndexOf("/") + 1);
                String encodedFileName = URLEncoder.encode(fileName, StandardCharsets.UTF_8.toString())
                        .replaceAll("\\+", "%20");
                
                HttpHeaders headers = new HttpHeaders();
                headers.setContentType(MediaType.IMAGE_JPEG);
                headers.setContentDispositionFormData("attachment", encodedFileName);
                headers.set("Access-Control-Expose-Headers", "Content-Disposition");
                
                return new ResponseEntity<>(content, headers, HttpStatus.OK);
            }
        } catch (Exception e) {
            log.error("下载隐患图片失败，文件路径：{}", filePath, e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}
