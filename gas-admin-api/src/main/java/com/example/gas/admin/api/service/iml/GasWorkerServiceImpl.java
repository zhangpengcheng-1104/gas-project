package com.example.gas.api.service.iml;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.example.gas.api.common.PageUtils;
import com.example.gas.api.db.dao.GasWorkerDao;
import com.example.gas.api.db.dao.MedicalDeptSubAndWorkerDao;
import com.example.gas.api.db.pojo.GasWorkerEntity;
import com.example.gas.api.db.pojo.MedicalDeptSubAndWorkerEntity;
import com.example.gas.api.service.GasWorkerService;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.CopyObjectArgs;
import io.minio.CopySource;
import io.minio.RemoveObjectArgs;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
@Slf4j
public class GasWorkerServiceImpl implements GasWorkerService {

    @Resource
    private GasWorkerDao gasWorkerDao;

    @Resource
    private MedicalDeptSubAndWorkerDao medicalDeptSubAndWorkerDao;

    @Value("${minio.endpoint}")
    private String endpoint;

    @Value("${minio.access-key}")
    private String accessKey;

    @Value("${minio.secret-key}")
    private String secretKey;

    @Override
    public PageUtils searchByPage(Map param) {
        ArrayList<HashMap> list = null;
        long count = gasWorkerDao.searchCount(param);
        if (count > 0) {
            list = gasWorkerDao.searchByPage(param);
        } else {
            list = new ArrayList<>();
        }
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }

    @Override
    public HashMap searchContent(int id) {
        HashMap map = gasWorkerDao.searchContent(id);
        normalizeLegacyPhoto(map);
        JSONArray tag = JSONUtil.parseArray(MapUtil.getStr(map, "tag"));
        map.replace("tag", tag);
        return map;
    }

    @Override
    public GasWorkerEntity searchById(Integer id) {
        GasWorkerEntity entity = gasWorkerDao.searchById(id);
        normalizeLegacyPhoto(entity);
        return entity;
    }

    @Override
    public HashMap searchByIdHashMap(int id) {
        HashMap map = gasWorkerDao.searchByIdHashMap(id);
        if (map != null) {
            normalizeLegacyPhoto(map);
            String tag = MapUtil.getStr(map, "tag");
            if (tag != null) {
                JSONArray array = JSONUtil.parseArray(tag);
                map.replace("tag", array);
            }
        }
        return map;
    }

    @Override
    public int insert(GasWorkerEntity entity) {
        // 生成UUID
        if (entity.getUuid() == null || entity.getUuid().isEmpty()) {
            entity.setUuid(IdUtil.simpleUUID().toUpperCase());
        }
        return gasWorkerDao.insert(entity);
    }

    @Override
    @Transactional
    public void insert(Map param) {
        GasWorkerEntity entity = BeanUtil.toBean(param, GasWorkerEntity.class);
        if (entity.getUuid() == null || entity.getUuid().isEmpty()) {
            entity.setUuid(IdUtil.simpleUUID().toUpperCase());
        }
        gasWorkerDao.insert(entity);

        String uuid = entity.getUuid();
        Integer workerId = gasWorkerDao.searchIdByUuid(uuid);

        String photo = MapUtil.getStr(param, "photo");
        if (photo != null && !photo.isEmpty() && workerId != null) {
            photo = processPhotoField(photo, workerId);
            Map<String, Object> photoParam = new HashMap<>();
            photoParam.put("id", workerId);
            photoParam.put("photo", photo);
            gasWorkerDao.updatePhoto(photoParam);
        }

        Integer subId = MapUtil.getInt(param, "subId");
        if (subId != null && workerId != null) {
            MedicalDeptSubAndWorkerEntity relationEntity = new MedicalDeptSubAndWorkerEntity();
            relationEntity.setDeptSubId(subId);
            relationEntity.setWorkerId(workerId);
            medicalDeptSubAndWorkerDao.insert(relationEntity);
        }
    }

    @Override
    public int update(GasWorkerEntity entity) {
        return gasWorkerDao.update(entity);
    }

    @Override
    @Transactional
    public void update(Map param) {
        Integer workerId = MapUtil.getInt(param, "id");
        String photo = MapUtil.getStr(param, "photo");
        
        if (photo != null && !photo.isEmpty() && workerId != null) {
            photo = processPhotoField(photo, workerId);
            param.put("photo", photo);
        }
        
        GasWorkerEntity entity = BeanUtil.toBean(param, GasWorkerEntity.class);
        gasWorkerDao.update(entity);

        Integer subId = MapUtil.getInt(param, "subId");
        if (workerId != null && subId != null) {
            Map<String, Object> relationParam = new HashMap<>();
            relationParam.put("workerId", workerId);
            relationParam.put("subId", subId);
            medicalDeptSubAndWorkerDao.updateWorkerSubDept(relationParam);
        }
    }

    @Override
    public int deleteById(Integer id) {
        return gasWorkerDao.deleteById(id);
    }

    @Override
    public int deleteByIds(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        return gasWorkerDao.deleteByIds(ids);
    }

    @Override
    public Integer getNextId() {
        Integer maxId = gasWorkerDao.getMaxId();
        return maxId == null ? 1 : maxId + 1;
    }

    @Override
    @Transactional
    public void updatePhoto(MultipartFile file, Integer workerId) {
        try {
            String filename = "worker-" + workerId + ".jpg";
            // 在Minio中保存照片
            MinioClient client = MinioClient.builder()
                    .endpoint(endpoint)
                    .credentials(accessKey, secretKey)
                    .build();

            client.putObject(
                    PutObjectArgs.builder()
                            .bucket("worker")
                            .object("/" + filename)
                            .stream(file.getInputStream(), -1, 5 * 1024 * 1024)
                            .contentType("image/jpeg")
                            .build()
            );

            // 更新表photo字段
            String photoUrl = "/" + filename;
            Map<String, Object> param = new HashMap<>();
            param.put("id", workerId);
            param.put("photo", photoUrl);
            gasWorkerDao.updatePhoto(param);
        } catch (Exception e) {
            log.error("保存照片失败", e);
            throw new RuntimeException("保存照片失败");
        }
    }

    @Override
    @Transactional
    public void updatePhoto(String photo, Integer workerId) {
        Map<String, Object> param = new HashMap<>();
        param.put("id", workerId);
        param.put("photo", photo);
        gasWorkerDao.updatePhoto(param);
    }

    @Override
    public ArrayList<HashMap> searchByDeptSubId(Integer deptSubId) {
        return gasWorkerDao.searchByDeptSubId(deptSubId);
    }

    @Override
    public ArrayList<HashMap> searchAll() {
        return gasWorkerDao.searchAll();
    }

    private void normalizeLegacyPhoto(HashMap map) {
        if (map == null) {
            return;
        }
        String photo = MapUtil.getStr(map, "photo");
        if (isLegacyPhoto(photo)) {
            map.put("photo", null);
        }
    }

    private void normalizeLegacyPhoto(GasWorkerEntity entity) {
        if (entity == null) {
            return;
        }
        if (isLegacyPhoto(entity.getPhoto())) {
            entity.setPhoto(null);
        }
    }

    private boolean isLegacyPhoto(String photo) {
        if (photo == null) {
            return false;
        }
        String value = photo.trim().toLowerCase();
        if (value.isEmpty()) {
            return false;
        }
        return value.startsWith("/doctor")
                || value.startsWith("doctor/")
                || value.startsWith("doctor-");
    }

    private String processPhotoField(String photo, Integer workerId) {
        if (photo == null || photo.isEmpty()) {
            return photo;
        }
        
        try {
            String objectName = photo;
            if (photo.startsWith("http://") || photo.startsWith("https://")) {
                if (photo.contains("/worker/")) {
                    objectName = photo.substring(photo.indexOf("/worker/") + 8);
                } else {
                    return photo;
                }
            } else if (photo.startsWith("/")) {
                objectName = photo.substring(1);
            }
            
            String targetName = "worker-" + workerId + ".jpg";
            
            if (!objectName.equals(targetName)) {
                MinioClient client = MinioClient.builder()
                        .endpoint(endpoint)
                        .credentials(accessKey, secretKey)
                        .build();
                
                client.copyObject(
                    CopyObjectArgs.builder()
                        .bucket("worker")
                        .object(targetName)
                        .source(
                            CopySource.builder()
                                .bucket("worker")
                                .object(objectName)
                                .build()
                        )
                        .build()
                );
                
                client.removeObject(
                    RemoveObjectArgs.builder()
                        .bucket("worker")
                        .object(objectName)
                        .build()
                );
                
                return "/" + targetName;
            }
            
            return "/" + objectName;
        } catch (Exception e) {
            log.error("处理照片文件失败", e);
            return photo;
        }
    }
}
