package com.example.gas.patient.wx.api.service;

import com.example.gas.patient.wx.api.db.pojo.UserInfoCardEntity;
import java.util.HashMap;

public interface UserInfoCardService {
    public void insert(UserInfoCardEntity entity);
    
    public HashMap searchUserInfoCard(int userId);
}
