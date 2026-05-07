package com.example.gas.api.service;

import java.util.Map;

public interface GasWorkerInspectionReportService {
    
    byte[] generateInspectionReport(Map param) throws Exception;
    
    byte[] generateInspectionReportByPlanId(Integer planId) throws Exception;
    
    String generateAndUploadReport(Integer planId) throws Exception;
}
