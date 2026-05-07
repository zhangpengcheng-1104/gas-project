package com.example.gas.api.controller.form;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import java.util.Date;

@Data
public class InsertGasWorkerForm {
    @NotBlank(message = "name不能为空")
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{1,30}$", message = "name内容不正确")
    private String name;

    @Pattern(regexp = "(^\\d{15}$)|(^\\d{17}(\\d|X|x)$)", message = "pid格式不正确")
    private String pid;

    private String uuid;

    @Pattern(regexp = "^男$|^女$", message = "sex内容不正确")
    private String sex;

    private String photo;

    private Date birthday;

    @Pattern(regexp = "^[\\u4e00-\\u9fa5a-zA-Z]{1,50}$", message = "school内容不正确")
    private String school;

    @Pattern(regexp = "^小学$|^中学$|^大专$|^大学$", message = "degree内容不正确")
    private String degree;

    @Pattern(regexp = "^1[3-9]\\d{9}$", message = "tel格式不正确")
    private String tel;

    private String address;

    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\\.[a-zA-Z]{2,}$", message = "email格式不正确")
    private String email;

    @Pattern(regexp = "^助工$|^工程师$|^高工$|^总工$", message = "job内容不正确")
    private String job;

    private String remark;

    private String description;

    private Date hiredate;

    private String tag;

    private Boolean recommended;

    @NotNull(message = "status不能为空")
    private Byte status;

    @NotNull(message = "deptId不能为空")
    @Min(value = 1, message = "deptId不能小于1")
    private Integer deptId;

    @NotNull(message = "subId不能为空")
    @Min(value = 1, message = "subId不能小于1")
    private Integer subId;
}
