package com.example.gas.patient.wx.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import com.example.gas.patient.wx.api.common.PageUtils;
import com.example.gas.patient.wx.api.common.R;
import com.example.gas.patient.wx.api.service.GasHazardInfoService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
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

    @PostMapping("/searchById")
    public R searchById(@RequestBody Map<String, Object> params) {
        Integer id = Integer.parseInt(params.get("id").toString());
        HashMap map = gasHazardInfoService.searchById(id);
        if (map == null) {
            return R.error("隐患信息不存在");
        }
        return R.ok().put("result", map);
    }

    @PostMapping("/wx/searchByAssigneeId")
    public R wxSearchByAssigneeId(@RequestBody Map<String, Object> params) {
        Integer assigneeId = params.get("assigneeId") != null ? Integer.parseInt(params.get("assigneeId").toString()) : null;
        Integer rectifyStatus = params.get("rectifyStatus") != null ? Integer.parseInt(params.get("rectifyStatus").toString()) : null;
        
        if (assigneeId == null) {
            return R.error().put("msg", "指派人ID不能为空");
        }
        
        int page = params.get("page") != null ? Integer.parseInt(params.get("page").toString()) : 1;
        int length = params.get("length") != null ? Integer.parseInt(params.get("length").toString()) : 10;
        int start = (page - 1) * length;
        
        Map<String, Object> param = new HashMap<>();
        param.put("assigneeId", assigneeId);
        param.put("rectifyStatus", rectifyStatus);
        param.put("start", start);
        param.put("length", length);
        param.put("page", page);
        
        PageUtils pageUtils = gasHazardInfoService.searchByAssigneeId(param);
        return R.ok().put("result", pageUtils);
    }

    @PostMapping("/wx/submitRectification")
    public R wxSubmitRectification(@RequestBody Map<String, Object> params) {
        Integer id = params.get("id") != null ? Integer.parseInt(params.get("id").toString()) : null;
        
        if (id == null) {
            return R.error().put("msg", "隐患ID不能为空");
        }
        
        Map<String, Object> param = new HashMap<>();
        param.put("id", id);
        param.put("rectifyStatus", 1);
        param.put("rectifyTime", new Date());
        
        if (params.get("rectifyPhoto") != null) {
            param.put("rectifyPhoto", params.get("rectifyPhoto").toString());
        }
        if (params.get("rectifyVideo") != null) {
            param.put("rectifyVideo", params.get("rectifyVideo").toString());
        }
        
        gasHazardInfoService.update(param);
        return R.ok().put("msg", "整改提交成功");
    }

    @PostMapping("/wx/uploadRectificationPhoto")
    public R wxUploadRectificationPhoto(@RequestParam("file") MultipartFile file,
                                        @RequestParam("hazardId") Integer hazardId,
                                        @RequestParam(value = "index", required = false) Integer index) {
        if (file.isEmpty()) {
            return R.error().put("msg", "文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        
        if (!suffix.equalsIgnoreCase(".jpg") && !suffix.equalsIgnoreCase(".jpeg") 
            && !suffix.equalsIgnoreCase(".png")) {
            return R.error().put("msg", "只支持JPG、JPEG、PNG格式的图片");
        }

        try {
            String filename = "photo_" + (index != null ? index : System.currentTimeMillis()) + suffix;
            String objectName = "/" + hazardId + "/" + filename;

            MinioClient client = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            client.putObject(
                    PutObjectArgs.builder()
                            .bucket("rectification")
                            .object(objectName)
                            .stream(file.getInputStream(), -1, 10 * 1024 * 1024)
                            .contentType(file.getContentType())
                            .build()
            );

            Map<String, Object> result = new HashMap<>();
            result.put("path", objectName);
            
            return R.ok().put("result", result);
        } catch (Exception e) {
            log.error("上传整改图片失败", e);
            return R.error().put("msg", "文件上传失败: " + e.getMessage());
        }
    }

    @PostMapping("/wx/uploadRectificationVideo")
    public R wxUploadRectificationVideo(@RequestParam("file") MultipartFile file,
                                        @RequestParam("hazardId") Integer hazardId) {
        if (file.isEmpty()) {
            return R.error().put("msg", "文件不能为空");
        }

        String originalFilename = file.getOriginalFilename();
        String suffix = originalFilename.substring(originalFilename.lastIndexOf("."));
        
        if (!suffix.equalsIgnoreCase(".mp4") && !suffix.equalsIgnoreCase(".mov") 
            && !suffix.equalsIgnoreCase(".avi")) {
            return R.error().put("msg", "只支持MP4、MOV、AVI格式的视频");
        }

        try {
            String filename = "video" + suffix;
            String objectName = "/" + hazardId + "/" + filename;

            MinioClient client = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            client.putObject(
                    PutObjectArgs.builder()
                            .bucket("rectification")
                            .object(objectName)
                            .stream(file.getInputStream(), -1, 50 * 1024 * 1024)
                            .contentType(file.getContentType())
                            .build()
            );

            Map<String, Object> result = new HashMap<>();
            result.put("path", objectName);
            
            return R.ok().put("result", result);
        } catch (Exception e) {
            log.error("上传整改视频失败", e);
            return R.error().put("msg", "文件上传失败: " + e.getMessage());
        }
    }
}
