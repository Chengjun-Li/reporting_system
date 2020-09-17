package com.antra.evaluation.reporting_system.pojo.api;

import javax.validation.constraints.NotEmpty;
import java.util.List;

public class ExcelRequest {
    @NotEmpty(message = "headers can not be empty")
    private List<String> headers;

    private String description;
    @NotEmpty(message = "data can not be empty")
    private List<List<String>> data;

    private String submitter;

    public List<List<String>> getData() {
        return data;
    }

    public void setData(List<List<String>> data) {
        this.data = data;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public List<String> getHeaders() {
        return headers;
    }

    public void setHeaders(List<String> headers) {
        this.headers = headers;
    }
}
