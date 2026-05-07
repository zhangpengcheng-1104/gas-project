package com.example.gas.api.service.iml;

import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.MD5;
import com.example.gas.api.common.PageUtils;
import com.example.gas.api.db.dao.MisUserDao;
import com.example.gas.api.db.pojo.MisUserEntity;
import com.example.gas.api.service.MisUserService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class MisUserServiceImpl implements MisUserService {

    @Resource
    private MisUserDao misUserDao;

    @Override
    public PageUtils searchByPage(Map param) {
        ArrayList<HashMap> list = null;
        long count = misUserDao.searchCount(param);
        if (count > 0) {
            list = misUserDao.searchByPage(param);
        } else {
            list = new ArrayList<>();
        }
        int page = MapUtil.getInt(param, "page");
        int length = MapUtil.getInt(param, "length");
        PageUtils pageUtils = new PageUtils(list, count, page, length);
        return pageUtils;
    }

    @Override
    public Integer login(Map param) {
        String username = MapUtil.getStr(param, "username");
        String password = MapUtil.getStr(param, "password");
        MD5 md5 = MD5.create();
        String temp = md5.digestHex(username);
        // first 6 characters
        String tempStart = StrUtil.subWithLength(temp, 0, 6);
        // last 3 characters
        String tempEnd = StrUtil.subSuf(temp, temp.length() - 3);
        // mix original password and hash encrypt
        password = md5.digestHex(tempStart + password + tempEnd);
        param.put("password", password);
        Integer userId = misUserDao.login(param);
        return userId;
    }

    @Override
    public List<MisUserEntity> searchUserList(Map param) {
        return misUserDao.searchUserList(param);
    }

    @Override
    public MisUserEntity searchUserById(Integer id) {
        return misUserDao.searchUserById(id);
    }

    @Override
    public int insertUser(MisUserEntity user) {
        // 密码加密
        String username = user.getUsername();
        String password = user.getPassword();
        MD5 md5 = MD5.create();
        String temp = md5.digestHex(username);
        String tempStart = StrUtil.subWithLength(temp, 0, 6);
        String tempEnd = StrUtil.subSuf(temp, temp.length() - 3);
        password = md5.digestHex(tempStart + password + tempEnd);
        user.setPassword(password);
        return misUserDao.insertUser(user);
    }

    @Override
    public int updateUser(MisUserEntity user) {
        // 如果密码不为空，则重新加密
        if (StrUtil.isNotEmpty(user.getPassword())) {
            String username = user.getUsername();
            String password = user.getPassword();
            MD5 md5 = MD5.create();
            String temp = md5.digestHex(username);
            String tempStart = StrUtil.subWithLength(temp, 0, 6);
            String tempEnd = StrUtil.subSuf(temp, temp.length() - 3);
            password = md5.digestHex(tempStart + password + tempEnd);
            user.setPassword(password);
        }
        return misUserDao.updateUser(user);
    }

    @Override
    public int deleteUser(Integer id) {
        // 先删除用户角色关联
        misUserDao.deleteUserRole(id);
        // 再删除用户
        return misUserDao.deleteUser(id);
    }

    @Override
    public int batchDeleteUsers(List<Integer> ids) {
        if (ids == null || ids.isEmpty()) {
            return 0;
        }
        // 批量删除用户角色关联
        misUserDao.batchDeleteUserRoles(ids);
        // 批量删除用户
        return misUserDao.batchDeleteUsers(ids);
    }

    @Override
    public List<Integer> searchUserRoles(Integer userId) {
        return misUserDao.searchUserRoles(userId);
    }

    @Override
    public int saveUserRoles(Integer userId, List<Integer> roles) {
        // 先删除原有角色
        misUserDao.deleteUserRole(userId);
        // 再插入新角色
        for (Integer roleId : roles) {
            Map<String, Object> param = new HashMap<>();
            param.put("userId", userId);
            param.put("roleId", roleId);
            misUserDao.insertUserRole(param);
        }
        return 1;
    }

    @Override
    public int updatePassword(Integer userId, String oldPassword, String newPassword) {
        // 获取用户信息
        MisUserEntity user = misUserDao.searchUserById(userId);
        if (user == null) {
            return 0;
        }
        
        // 验证原密码
        String username = user.getUsername();
        MD5 md5 = MD5.create();
        String temp = md5.digestHex(username);
        String tempStart = StrUtil.subWithLength(temp, 0, 6);
        String tempEnd = StrUtil.subSuf(temp, temp.length() - 3);
        String encryptedOldPassword = md5.digestHex(tempStart + oldPassword + tempEnd);
        
        if (!encryptedOldPassword.equals(user.getPassword())) {
            return 0;
        }
        
        // 更新新密码
        String encryptedNewPassword = md5.digestHex(tempStart + newPassword + tempEnd);
        user.setPassword(encryptedNewPassword);
        return misUserDao.updateUser(user);
    }

    @Override
    public Integer getNextId() {
        Integer maxId = misUserDao.getMaxId();
        return maxId == null ? 1 : maxId + 1;
    }
}