package com.example.gas.api.db.pojo;

import lombok.Data;

import java.io.Serializable;

@Data
public class MedicalDeptEntity implements Serializable {
    private static final long serialVersionUID = 1L;

    private Integer id;
    private String name;
    private Integer outpatient;
    private String description;
    private Integer recommended;
}
