package com.example.gas.patient.wx.api.db.dao;

import com.example.gas.patient.wx.api.db.pojo.UserInfoCardEntity;

public interface UserInfoCardDao {
    public String searchUserTel(int userId);
    
    public UserInfoCardEntity searchByUserId(int userId);
    
    public void insert(UserInfoCardEntity entity);
}
