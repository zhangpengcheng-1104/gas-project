package com.example.gas.api.service.iml;

import com.example.gas.api.db.dao.MisRoleDao;
import com.example.gas.api.db.pojo.MisRoleEntity;
import com.example.gas.api.service.MisRoleService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MisRoleServiceImpl implements MisRoleService {

    @Resource
    private MisRoleDao misRoleDao;

    @Override
    public List<MisRoleEntity> searchRoleList(Map param) {
        return misRoleDao.searchRoleList(param);
    }

    @Override
    public MisRoleEntity searchRoleById(Integer id) {
        return misRoleDao.searchRoleById(id);
    }

    @Override
    public int insertRole(MisRoleEntity role) {
        return misRoleDao.insertRole(role);
    }

    @Override
    public int updateRole(MisRoleEntity role) {
        return misRoleDao.updateRole(role);
    }

    @Override
    public int deleteRole(Integer id) {
        misRoleDao.deleteRolePermission(id);
        return misRoleDao.deleteRole(id);
    }

    @Override
    public int batchDeleteRole(List<Integer> ids) {
        for (Integer id : ids) {
            misRoleDao.deleteRolePermission(id);
        }
        return misRoleDao.batchDeleteRole(ids);
    }

    @Override
    public List<String> searchRolePermissions(Integer roleId) {
        return misRoleDao.searchRolePermissions(roleId);
    }

    @Override
    public int saveRolePermission(Integer roleId, List<String> permissions) {
        // 先删除原有权限
        misRoleDao.deleteRolePermission(roleId);
        // 获取当前最大权限ID并加1作为新ID
        Integer maxPermissionId = misRoleDao.getMaxPermissionId();
        int currentId = maxPermissionId == null ? 1 : maxPermissionId + 1;
        // 再插入新权限
        for (String permission : permissions) {
            Map<String, Object> param = new HashMap<>();
            param.put("id", currentId++);
            param.put("roleId", roleId);
            param.put("permission", permission);
            misRoleDao.insertRolePermission(param);
        }
        return 1;
    }

    @Override
    public Integer getNextId() {
        Integer maxId = misRoleDao.getMaxId();
        return maxId == null ? 1 : maxId + 1;
    }

    @Override
    public int searchRolePermissionCount(Integer roleId) {
        return misRoleDao.searchRolePermissionCount(roleId);
    }

    @Override
    public int searchRoleUserCount(Integer roleId) {
        return misRoleDao.searchRoleUserCount(roleId);
    }

    @Override
    public ArrayList<HashMap> searchRoleListWithCount(Map param) {
        return (ArrayList<HashMap>) misRoleDao.searchRoleListWithCount(param);
    }

    @Override
    public int searchRoleCount(Map param) {
        return misRoleDao.searchRoleCount(param);
    }

    @Override
    public ArrayList<HashMap> searchAllPermissions() {
        return misRoleDao.searchAllPermissions();
    }

    @Override
    public ArrayList<HashMap> searchSimpleRoleList() {
        return misRoleDao.searchSimpleRoleList();
    }
}
