package com.example.gas.patient.wx.api.controller.form;

import lombok.Data;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Min;

@Data
public class SearchInspectionForm {

    @NotNull(message = "ID不能为空")
    @Min(value = 1, message = "ID无效")
    private Integer id;
}
