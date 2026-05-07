package com.example.gas.patient.wx.api.service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public interface GasWorkerPlanService {
    public ArrayList<HashMap> searchCanWorkInDateRange(Map param);
    
    public ArrayList<HashMap> searchWorkerPlanInDay(Map param);
}
