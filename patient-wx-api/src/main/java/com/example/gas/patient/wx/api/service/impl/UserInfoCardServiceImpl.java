package com.example.gas.patient.wx.api.service.impl;

import com.example.gas.patient.wx.api.db.dao.GasWorkerDao;
import com.example.gas.patient.wx.api.db.dao.UserInfoCardDao;
import com.example.gas.patient.wx.api.db.pojo.GasWorkerEntity;
import com.example.gas.patient.wx.api.db.pojo.UserInfoCardEntity;
import com.example.gas.patient.wx.api.service.UserInfoCardService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;
import java.util.HashMap;

@Service
public class UserInfoCardServiceImpl implements UserInfoCardService {
    @Resource
    private UserInfoCardDao userInfoCardDao;
    
    @Resource
    private GasWorkerDao gasWorkerDao;
    
    @Override
    @Transactional
    public void insert(UserInfoCardEntity entity) {
        String tel = entity.getTel();
        if (tel != null && !tel.isEmpty()) {
            GasWorkerEntity worker = gasWorkerDao.searchByTel(tel);
            if (worker != null) {
                entity.setWorkerId(worker.getId());
            }
        }
        userInfoCardDao.insert(entity);
    }
    
    @Override
    public HashMap searchUserInfoCard(int userId) {
        HashMap map = new HashMap();
        UserInfoCardEntity cardEntity = userInfoCardDao.searchByUserId(userId);
        if (cardEntity != null) {
            map.put("id", cardEntity.getId());
            map.put("name", cardEntity.getName());
            map.put("sex", cardEntity.getSex());
            map.put("pid", cardEntity.getPid());
            map.put("tel", cardEntity.getTel());
            map.put("birthday", cardEntity.getBirthday());
            map.put("medicalHistory", cardEntity.getMedicalHistory());
            map.put("insuranceType", cardEntity.getInsuranceType());
            map.put("workerId", cardEntity.getWorkerId());
            
            if (cardEntity.getWorkerId() != null) {
                GasWorkerEntity worker = gasWorkerDao.searchById(cardEntity.getWorkerId());
                if (worker != null) {
                    map.put("photo", worker.getPhoto());
                }
            }
        }
        return map;
    }
}
