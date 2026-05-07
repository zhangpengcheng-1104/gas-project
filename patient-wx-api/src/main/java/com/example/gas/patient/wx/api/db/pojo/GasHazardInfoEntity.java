package com.example.gas.patient.wx.api.db.pojo;

import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

@Data
public class GasHazardInfoEntity {
    private Integer id;
    private String hazardType;
    private String hazardLevel;
    private String hazardSource;
    private Integer userId;
    private String areaCode;
    private String address;
    private BigDecimal longitude;
    private BigDecimal latitude;
    private String hazardDesc;
    private Date rectifyStartDate;
    private Date rectifyEndDate;
    private String submitUserName;
    private Date submitTime;
    private Integer assigneeId;
    private Integer isSelfRectify;
    private String hazardPhoto;
    private Integer rectifyStatus;
    private String rectifyPhoto;
    private String rectifyVideo;
    private Date rectifyTime;
}
