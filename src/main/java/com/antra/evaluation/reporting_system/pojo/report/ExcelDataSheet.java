package com.antra.evaluation.reporting_system.pojo.report;


import java.util.List;

public class ExcelDataSheet {
    private String title;
    private List<ExcelDataHeader> headers;
    private List<List<String>> dataRows;

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public ExcelDataSheet() {
    }

    public ExcelDataSheet(String title, List<ExcelDataHeader> headers, List<List<String>> dataRows) {
        this.title = title;
        this.headers = headers;
        this.dataRows = dataRows;
    }

    public List<ExcelDataHeader> getHeaders() {
        return headers;
    }

    public void setHeaders(List<ExcelDataHeader> headers) {
        this.headers = headers;
    }

    public List<List<String>> getDataRows() {
        return dataRows;
    }

    public void setDataRows(List<List<String>> dataRows) {
        this.dataRows = dataRows;
    }
}
