package com.example.gas.patient.wx.api.service.impl;

import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONUtil;
import com.example.gas.patient.wx.api.db.dao.GasWorkerDao;
import com.example.gas.patient.wx.api.db.dao.UserInfoCardDao;
import com.example.gas.patient.wx.api.db.pojo.GasWorkerEntity;
import com.example.gas.patient.wx.api.db.pojo.UserInfoCardEntity;
import com.example.gas.patient.wx.api.service.GasWorkerService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;

@Service
public class GasWorkerServiceImpl implements GasWorkerService {
    @Resource
    private UserInfoCardDao userInfoCardDao;
    
    @Resource
    private GasWorkerDao gasWorkerDao;

    @Override
    public HashMap searchContentByUserId(int userId) {
        HashMap result = new HashMap();
        
        UserInfoCardEntity cardEntity = userInfoCardDao.searchByUserId(userId);
        if (cardEntity == null || cardEntity.getWorkerId() == null) {
            return result;
        }
        
        Integer workerId = cardEntity.getWorkerId();
        GasWorkerEntity worker = gasWorkerDao.searchById(workerId);
        if (worker == null) {
            return result;
        }
        
        result.put("id", worker.getId());
        result.put("name", worker.getName());
        result.put("sex", worker.getSex());
        result.put("pid", worker.getPid());
        result.put("tel", worker.getTel());
        result.put("birthday", worker.getBirthday());
        result.put("photo", worker.getPhoto());
        result.put("school", worker.getSchool());
        result.put("degree", worker.getDegree());
        result.put("address", worker.getAddress());
        result.put("email", worker.getEmail());
        result.put("job", worker.getJob());
        result.put("remark", worker.getRemark());
        result.put("description", worker.getDescription());
        result.put("hiredate", worker.getHiredate());
        
        String tag = worker.getTag();
        if (tag != null && !tag.isEmpty()) {
            try {
                JSONArray tagArray = JSONUtil.parseArray(tag);
                result.put("tag", tagArray);
            } catch (Exception e) {
                result.put("tag", tag);
            }
        }
        
        return result;
    }
}
