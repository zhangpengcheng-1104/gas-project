package com.example.gas.api.controller.form;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;

@Data
public class SearchGasWorkerByPageForm {
    @Pattern(regexp = "^[\\u4e00-\\u9fa5]{1,20}$", message = "name内容不正确")
    private String name;

    @Min(value = 1, message = "deptId不能小于1")
    private Integer deptId;

    @Pattern(regexp = "^小学$|^中学$|^大专$|^大学$", message = "degree内容不正确")
    private String degree;

    @Pattern(regexp = "^助工$|^工程师$|^高工$|^总工$", message = "job内容不正确")
    private String job;

    private Boolean recommended;

    @NotNull(message = "status不能为空")
    @Range(min = 1, max = 3, message = "status内容不正确")
    private Byte status;

    @Pattern(regexp = "^ASC$|^DESC$", message = "order内容不正确")
    private String order;

    @NotNull(message = "page不能为空")
    @Min(value = 1, message = "page不能小于1")
    private Integer page;

    @NotNull(message = "length不能为空")
    @Range(min = 10, max = 50, message = "length内容不正确")
    private Integer length;
}
