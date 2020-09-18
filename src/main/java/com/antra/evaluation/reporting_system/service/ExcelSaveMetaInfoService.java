package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;

public interface ExcelSaveMetaInfoService {
   ExcelResponse saveMetaInfo(ExcelFile excelFile);
}
