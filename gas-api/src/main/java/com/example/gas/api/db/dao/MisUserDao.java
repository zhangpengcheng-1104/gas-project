package com.example.gas.api.db.dao;

import com.example.gas.api.db.pojo.MisUserEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.HashMap;

@Mapper
public interface MisUserDao {
    Integer login(Map param);
    ArrayList<String> searchUserPermissions(int userId);
    List<MisUserEntity> searchUserList(Map param);
    ArrayList<HashMap> searchByPage(Map param);
    long searchCount(Map param);
    MisUserEntity searchUserById(Integer id);
    int insertUser(MisUserEntity user);
    int updateUser(MisUserEntity user);
    int deleteUser(Integer id);
    int batchDeleteUsers(List<Integer> ids);
    List<Integer> searchUserRoles(Integer userId);
    int insertUserRole(Map param);
    int deleteUserRole(Integer userId);
    int batchDeleteUserRoles(List<Integer> ids);
    Integer getMaxId();
}




