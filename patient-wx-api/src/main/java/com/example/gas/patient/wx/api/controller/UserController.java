package com.example.gas.patient.wx.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.map.MapUtil;
import com.example.gas.patient.wx.api.common.R;
import com.example.gas.patient.wx.api.controller.form.LoginOrRegisterForm;
import com.example.gas.patient.wx.api.service.UserService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequestMapping("/user")
public class UserController {
    @Resource
    private UserService userService;

    @PostMapping("/loginOrRegister")
    public R loginOrRegister(@RequestBody @Valid LoginOrRegisterForm form) {
        HashMap map = userService.loginOrRegister(form.getCode(), form.getNickname(), form.getPhoto(), form.getSex());
        String msg = MapUtil.getStr(map, "msg");
        Integer id = MapUtil.getInt(map, "id");
        StpUtil.login(id);
        String token = StpUtil.getTokenValue();
        R r = R.ok(msg).put("token", token);
        if (map.containsKey("tel")) {
            r.put("tel", MapUtil.getStr(map, "tel"));
        }
        return r;
    }

    @GetMapping("/searchUserInfo")
    public R searchUserInfo() {
        int userId = StpUtil.getLoginIdAsInt();
        HashMap map = userService.searchUserInfo(userId);
        return R.ok().put("result", map);
    }
}
