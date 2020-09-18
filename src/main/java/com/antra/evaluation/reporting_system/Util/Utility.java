package com.antra.evaluation.reporting_system.Util;

import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;

import java.io.File;

public class Utility {
    public static ExcelFile buildExcelFile(ExcelRequest request, ExcelData data, File file) {
        ExcelFile excelFile = new ExcelFile();
        excelFile.setDescription(request.getDescription());
        excelFile.setSubmitter(request.getSubmitter());
        excelFile.setGeneratedTime(data.getGeneratedTime());
        excelFile.setDownloadLink("/excel/" + data.getTitle() + "/content");
        excelFile.setFileId(data.getTitle());
        excelFile.setFileLocation(file.getAbsolutePath());
        excelFile.setNumOfSheets(data.getSheets().size());
        return excelFile;
    }
}
