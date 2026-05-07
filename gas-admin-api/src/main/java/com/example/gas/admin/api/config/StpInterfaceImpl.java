package com.example.gas.api.config;

import cn.dev33.satoken.stp.StpInterface;
import com.example.gas.api.db.dao.MisUserDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class StpInterfaceImpl implements StpInterface {
    @Autowired
    private MisUserDao userDao;

    /**
     * 返回一个用户所拥有的权限集合
     */
    @Override
    public List<String> getPermissionList(Object loginId, String loginKey) {
        int userId = Integer.parseInt(loginId.toString());
        System.out.println("LoginId: " + loginId);
        ArrayList<String> list = userDao.searchUserPermissions(userId);
        if (userId == 0) {
            list.add("ROOT");
        }
        System.out.println("Permissions: " + list);
        return list;
    }


    /**
     * 返回一个用户所拥有的角色标识集合
     */
    @Override
    public List<String> getRoleList(Object loginId, String loginKey) {
        int userId = Integer.parseInt(loginId.toString());
        List<Integer> roleIds = userDao.searchUserRoles(userId);
        List<String> roles = new ArrayList<>();
        for (Integer roleId : roleIds) {
            if (roleId == 0) {
                roles.add("ROOT");
            } else if (roleId == 1) {
                roles.add("MANAGER");
            } else if (roleId == 2) {
                roles.add("TECH");
            } else if (roleId == 3) {
                roles.add("MARKET");
            } else if (roleId == 4) {
                roles.add("FINANCE");
            }
        }
        return roles;
    }

}