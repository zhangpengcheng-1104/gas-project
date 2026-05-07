package com.example.gas.api.db.pojo;

import lombok.Data;

@Data
public class AreaNodeEntity {
    private String code;
    private String areaName;
    private String parentCode;
    private Integer level;
}
