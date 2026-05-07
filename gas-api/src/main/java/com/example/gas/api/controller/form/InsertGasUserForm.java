package com.example.gas.api.controller.form;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class InsertGasUserForm {
    @NotBlank(message = "用户户号不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9]{1,40}$", message = "用户户号格式不正确")
    private String userNo;

    @NotBlank(message = "户名不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z0-9]{1,40}$", message = "户名格式不正确")
    private String userName;

    @NotBlank(message = "用户类型不能为空")
    @Pattern(regexp = "^RESIDENT$|^NON_RESIDENT$", message = "用户类型内容不正确")
    private String userType;

    private String areaCode;
    private String areaName;

    @NotBlank(message = "详细地址不能为空")
    private String address;

    private String buildingInfo;

    @NotBlank(message = "联系电话不能为空")
    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "联系电话格式不正确")
    private String contactPhone;

    @Pattern(regexp = "(^\\d{15}$)|(^\\d{17}(\\d|X|x)$)", message = "证件号码格式不正确")
    private String idCard;

    @NotNull(message = "状态不能为空")
    private Byte status;

    private Long orgId;
    private String remark;
}
