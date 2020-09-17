package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;


import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ExcelService {
    InputStream getExcelBodyById(String id) throws FileNotFoundException;

    ExcelFile getExcelFileById(String id);

    public File generateExcelReport(ExcelData data) throws IOException;

    public ExcelFile saveExcelFileInfo(ExcelFile file);

    public String getNextId();

    void deleteExcel(ExcelFile file);

    List<ExcelFile> getFiles();
}
