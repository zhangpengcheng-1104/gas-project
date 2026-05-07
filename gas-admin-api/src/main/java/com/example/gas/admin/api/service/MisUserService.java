package com.example.gas.api.service;

import com.example.gas.api.common.PageUtils;
import com.example.gas.api.db.pojo.MisUserEntity;

import java.util.List;
import java.util.Map;

public interface MisUserService {
    public PageUtils searchByPage(Map param);
    Integer login(Map param);
    List<MisUserEntity> searchUserList(Map param);
    MisUserEntity searchUserById(Integer id);
    int insertUser(MisUserEntity user);
    int updateUser(MisUserEntity user);
    int deleteUser(Integer id);
    int batchDeleteUsers(List<Integer> ids);
    List<Integer> searchUserRoles(Integer userId);
    int saveUserRoles(Integer userId, List<Integer> roles);
    int updatePassword(Integer userId, String oldPassword, String newPassword);
    Integer getNextId();
}