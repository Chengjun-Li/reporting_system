package com.antra.evaluation.reporting_system.pojo.api;

import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;

public class ExcelResponse {
    ExcelFile file;

    public ExcelFile getFile() {
        return file;
    }

    public void setFile(ExcelFile file) {
        this.file = file;
    }
}
