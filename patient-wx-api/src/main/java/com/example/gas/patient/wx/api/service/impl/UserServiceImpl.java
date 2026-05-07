package com.example.gas.patient.wx.api.service.impl;

import cn.hutool.core.bean.BeanUtil;
import cn.hutool.core.map.MapUtil;
import cn.hutool.http.HttpUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.example.gas.patient.wx.api.db.dao.UserDao;
import com.example.gas.patient.wx.api.db.dao.UserInfoCardDao;
import com.example.gas.patient.wx.api.db.pojo.UserEntity;
import com.example.gas.patient.wx.api.db.pojo.UserInfoCardEntity;
import com.example.gas.patient.wx.api.service.UserService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.HashMap;

@Service
public class UserServiceImpl implements UserService {
    @Value("${wechat.app-id}")
    private String appId;

    @Value("${wechat.app-secret}")
    private String appSecret;
    
    @Resource
    private UserDao userDao;

    @Resource
    private UserInfoCardDao userInfoCardDao;
    
    @Override
    public HashMap loginOrRegister(String code, String nickname, String photo, String sex) {
        String openId = this.getOpenId(code);
        
        HashMap map = new HashMap();
        Integer id = userDao.searchAlreadyRegistered(openId);
        if (id == null) {
            UserEntity entity = new UserEntity();
            entity.setOpenId(openId);
            entity.setNickname(nickname);
            entity.setPhoto(photo);
            entity.setSex(sex);
            entity.setStatus((byte) 1);
            userDao.insert(entity); 
            id = userDao.searchAlreadyRegistered(entity.getOpenId());
            map.put("msg", "注册成功");
        }
        else{
            map.put("msg", "登陆成功");
        }
        map.put("id", id);
        
        UserInfoCardEntity cardEntity = userInfoCardDao.searchByUserId(id);
        if (cardEntity != null) {
            map.put("tel", cardEntity.getTel());
        }
        
        return map;
    }

    @Override
    public HashMap searchUserInfo(int userId) {
        HashMap map = new HashMap();
        UserEntity userEntity = userDao.searchById(userId);
        if (userEntity != null) {
            map.put("id", userEntity.getId());
            map.put("nickname", userEntity.getNickname());
            map.put("photo", userEntity.getPhoto());
        }
        
        UserInfoCardEntity cardEntity = userInfoCardDao.searchByUserId(userId);
        if (cardEntity != null) {
            map.put("name", cardEntity.getName());
            map.put("tel", cardEntity.getTel());
            map.put("sex", cardEntity.getSex());
            map.put("pid", cardEntity.getPid());
            map.put("birthday", cardEntity.getBirthday());
            if (cardEntity.getWorkerId() != null) {
                map.put("workerId", cardEntity.getWorkerId());
            }
        }
        
        return map;
    }

    private String getOpenId(String code) {
        String url = "https://api.weixin.qq.com/sns/jscode2session";
        HashMap map = new HashMap();
        map.put("appid", appId);
        map.put("secret", appSecret);
        map.put("js_code", code);
        map.put("grant_type", "authorization_code");
        String response = HttpUtil.post(url, map);
        JSONObject json = JSONUtil.parseObj(response);
        String openId = json.getStr("openid");
        if (openId == null || openId.length() == 0) {
            throw new RuntimeException("临时登陆凭证错误");
        }

        return openId;
    }
}
