package com.example.gas.api.db.pojo;

import lombok.Data;

import java.util.Date;

@Data
public class GasWorkerEntity {
    private Integer id;
    private String name;
    private String pid;
    private String uuid;
    private String sex;
    private String photo;
    private Date birthday;
    private String school;
    private String degree;
    private String tel;
    private String address;
    private String email;
    private String job;
    private String remark;
    private String description;
    private Date hiredate;
    private String tag;
    private Boolean recommended;
    private Byte status;
    private Date createTime;
    private Integer deptId;
    private Integer subId;
}
