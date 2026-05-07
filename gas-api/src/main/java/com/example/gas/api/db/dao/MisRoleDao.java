package com.example.gas.api.db.dao;

import com.example.gas.api.db.pojo.MisRoleEntity;
import org.apache.ibatis.annotations.Mapper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Mapper
public interface MisRoleDao {
    List<MisRoleEntity> searchRoleList(Map param);
    MisRoleEntity searchRoleById(Integer id);
    int insertRole(MisRoleEntity role);
    int updateRole(MisRoleEntity role);
    int deleteRole(Integer id);
    int batchDeleteRole(List<Integer> ids);
    List<String> searchRolePermissions(Integer roleId);
    int insertRolePermission(Map param);
    int deleteRolePermission(Integer roleId);
    Integer getMaxId();
    Integer getMaxPermissionId();
    int searchRolePermissionCount(Integer roleId);
    int searchRoleUserCount(Integer roleId);
    List<HashMap> searchRoleListWithCount(Map param);
    int searchRoleCount(Map param);
    ArrayList<HashMap> searchAllPermissions();
    ArrayList<HashMap> searchSimpleRoleList();
}
