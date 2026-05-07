package com.example.gas.patient.wx.api.controller;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.IdUtil;
import cn.hutool.json.JSONUtil;
import com.example.gas.patient.wx.api.common.R;
import com.example.gas.patient.wx.api.controller.form.InsertUserInfoCardForm;
import com.example.gas.patient.wx.api.db.pojo.UserInfoCardEntity;
import com.example.gas.patient.wx.api.service.UserInfoCardService;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.HashMap;

@RestController
@RequestMapping("/user/info/card")
public class UserInfoCardController {
    @Resource
    public UserInfoCardService userInfoCardService;

    @PostMapping("/insert")
    public R insert(@RequestBody @Valid InsertUserInfoCardForm form) {
        UserInfoCardEntity entity = new UserInfoCardEntity();
        entity.setName(form.getName());
        entity.setSex(form.getSex());
        entity.setPid(form.getPid());
        entity.setTel(form.getTel());
        entity.setBirthday(form.getBirthday());
        entity.setInsuranceType(form.getInsuranceType());
        
        int userId = StpUtil.getLoginIdAsInt();
        entity.setUserId(userId);
        entity.setUuid(IdUtil.simpleUUID());
        String json = JSONUtil.parseArray(form.getMedicalHistory()).toString();
        entity.setMedicalHistory(json);
        
        userInfoCardService.insert(entity);
        return R.ok();
    }
    
    @GetMapping("/searchUserInfoCard")
    public R searchUserInfoCard() {
        int userId = StpUtil.getLoginIdAsInt();
        HashMap map = userInfoCardService.searchUserInfoCard(userId);
        return R.ok().put("result", map);
    }
}
