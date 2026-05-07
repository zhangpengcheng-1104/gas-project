package com.example.gas.api.db.pojo;

import lombok.Data;
import java.util.Date;

@Data
public class GasUserEntity {
    private Long id;
    private String userNo;
    private String userName;
    private String userType;
    private String areaCode;
    private String areaName;
    private String address;
    private String buildingInfo;
    private String contactPhone;
    private String idCard;
    private Byte status;
    private Long orgId;
    private Date createTime;
    private Long createId;
    private Date updateTime;
    private Long updateId;
    private Boolean deleted;
    private String remark;
}
