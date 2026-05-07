package com.example.gas.patient.wx.api.db.dao;

import com.example.gas.patient.wx.api.db.pojo.UserEntity;
import org.springframework.transaction.annotation.Transactional;

public interface UserDao {
    @Transactional
    public int insert(UserEntity entity);
    
    public Integer searchAlreadyRegistered(String openId);
    
    public UserEntity searchById(int id);
}
