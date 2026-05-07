package com.example.gas.api.controller.form;

import lombok.Data;
import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Max;

@Data
public class SearchMisUserByPageForm {
    @NotNull(message = "page不能为空")
    @Min(value = 1, message = "page不能小于1")
    private Integer page;

    @NotNull(message = "length不能为空")
    @Min(value = 10, message = "length不能小于10")
    @Max(value = 50, message = "length不能大于50")
    private Integer length;

    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{1,10}$", message = "name内容不正确")
    private String name;
    
    @Pattern(regexp = "^男$|^女$", message = "sex内容不正确")
    private String sex;
    
    @Min(value = 1, message = "dept不能小于1")
    private Integer deptId;

    @Pattern(regexp = "^[a-zA-Z0-9\\u4e00-\\u9fa5]{2,10}$", message = "role内容不正确")
    private String role;

    @Min(value = 1, message = "status不能小于1")
    private Integer status;

}