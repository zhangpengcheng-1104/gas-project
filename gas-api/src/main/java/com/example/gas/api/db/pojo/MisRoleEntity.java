package com.example.gas.api.db.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MisRoleEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String roleName;
    private String remark;

}
