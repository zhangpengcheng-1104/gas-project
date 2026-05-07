package com.example.gas.api.service;

import com.example.gas.api.db.pojo.MisRoleEntity;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public interface MisRoleService {
    List<MisRoleEntity> searchRoleList(Map param);
    MisRoleEntity searchRoleById(Integer id);
    int insertRole(MisRoleEntity role);
    int updateRole(MisRoleEntity role);
    int deleteRole(Integer id);
    int batchDeleteRole(List<Integer> ids);
    List<String> searchRolePermissions(Integer roleId);
    int saveRolePermission(Integer roleId, List<String> permissions);
    Integer getNextId();
    int searchRolePermissionCount(Integer roleId);
    int searchRoleUserCount(Integer roleId);
    ArrayList<HashMap> searchRoleListWithCount(Map param);
    int searchRoleCount(Map param);
    ArrayList<HashMap> searchAllPermissions();
    ArrayList<HashMap> searchSimpleRoleList();
}
