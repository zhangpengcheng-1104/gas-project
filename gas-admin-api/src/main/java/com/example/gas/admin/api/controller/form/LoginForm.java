package com.example.gas.api.controller.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Pattern;

@Data
public class LoginForm {
    @NotBlank(message = "username不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5_]+$", message = "username仅支持字母、数字、中文和下划线")
    @ByteLength(min = 2, max = 18, message = "username长度需在2~18字节之间")
    private String username;

    @NotBlank(message = "password不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{6,20}$", message = "password内容不正确")
    private String password;
}