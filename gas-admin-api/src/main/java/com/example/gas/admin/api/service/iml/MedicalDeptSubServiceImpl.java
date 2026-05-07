package com.example.gas.api.service.iml;

import com.example.gas.api.db.dao.MedicalDeptSubDao;
import com.example.gas.api.service.MedicalDeptSubService;
import org.springframework.stereotype.Service;

import javax.annotation.Resource;
import java.util.ArrayList;
import java.util.HashMap;

@Service
public class MedicalDeptSubServiceImpl implements MedicalDeptSubService {

    @Resource
    private MedicalDeptSubDao medicalDeptSubDao;

    @Override
    public ArrayList<HashMap> searchByDeptId(Integer deptId) {
        ArrayList<HashMap> list = medicalDeptSubDao.searchByDeptId(deptId);
        return list;
    }
}
