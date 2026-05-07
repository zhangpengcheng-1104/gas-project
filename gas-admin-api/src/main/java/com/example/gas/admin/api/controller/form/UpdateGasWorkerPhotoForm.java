package com.example.gas.api.controller.form;

import lombok.Data;

import javax.validation.constraints.Min;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UpdateGasWorkerPhotoForm {
    @NotNull(message = "workerId不能为空")
    @Min(value = 1, message = "workerId不能小于1")
    private Integer workerId;

    @NotBlank(message = "photo不能为空")
    private String photo;
}
