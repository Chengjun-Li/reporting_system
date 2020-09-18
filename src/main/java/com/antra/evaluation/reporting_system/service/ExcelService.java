package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.pojo.api.ExcelFileId;
import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;


import javax.servlet.http.HttpServletResponse;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

public interface ExcelService {

    ExcelResponse createExcel(ExcelRequest request) throws IOException;

    ExcelResponse createMultiSheetExcel(MultiSheetExcelRequest request) throws IOException;

    List<ExcelResponse> createMultipleExcels(List<ExcelRequest> requestList) throws IOException;

    InputStream getExcelBodyById(String id) throws FileNotFoundException;

    void downloadZipFile(List<ExcelFileId> fileIds, HttpServletResponse response) throws IOException;

    ExcelFile getExcelFileById(String id);

    void deleteExcel(ExcelFile file);

    List<ExcelFile> getFiles();

}
