package com.antra.evaluation.reporting_system.pojo.api;

import com.fasterxml.jackson.annotation.JsonProperty;

public class ResponseMessage {
    private String message;

    @JsonProperty(value = "Excel file info")
    private ExcelResponse excelResponse;

    public String getMessage() {
        return message;
    }

    public ResponseMessage(String message, ExcelResponse excelResponse) {
        this.message = message;
        this.excelResponse = excelResponse;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public ExcelResponse getExcelResponse() {
        return excelResponse;
    }

    public void setExcelResponse(ExcelResponse excelResponse) {
        this.excelResponse = excelResponse;
    }

}
