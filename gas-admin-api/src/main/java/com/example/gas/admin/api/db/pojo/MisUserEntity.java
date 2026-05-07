package com.example.gas.api.db.pojo;

import com.example.gas.api.controller.form.ByteLength;
import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class MisUserEntity {
    private Integer id;

    @NotBlank(message = "username不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5_]+$", message = "username仅支持字母、数字、中文和下划线")
    @ByteLength(min = 2, max = 18, message = "username长度需在2~18字节之间")
    private String username;

    private String password;
    private String name;
    private String sex;
    private String tel;
    private String email;
    private Integer deptId;
    private String job;
    private Integer refId;
    private Byte status;
    private String createTime;
}