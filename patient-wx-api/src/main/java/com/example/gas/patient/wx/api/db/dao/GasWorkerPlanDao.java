package com.example.gas.patient.wx.api.db.dao;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface GasWorkerPlanDao {
    public ArrayList<String> searchCanWorkInDateRange(Map param);
    
    public ArrayList<HashMap> searchWorkerPlanInDay(Map param);
}
