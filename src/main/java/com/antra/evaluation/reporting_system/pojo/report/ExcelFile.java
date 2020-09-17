package com.antra.evaluation.reporting_system.pojo.report;

import java.time.LocalDateTime;

public class ExcelFile {
    private String fileId;

    private String description;

    private String submitter;

    private String downloadLink;

    private LocalDateTime generatedTime;

    private String fileLocation;

    private int numOfSheets;

    public int getNumOfSheets() {
        return numOfSheets;
    }

    public void setNumOfSheets(int numOfSheets) {
        this.numOfSheets = numOfSheets;
    }

    public String getFileLocation() {
        return fileLocation;
    }

    public void setFileLocation(String fileLocation) {
        this.fileLocation = fileLocation;
    }

    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getSubmitter() {
        return submitter;
    }

    public void setSubmitter(String submitter) {
        this.submitter = submitter;
    }

    public String getDownloadLink() {
        return downloadLink;
    }

    public void setDownloadLink(String downloadLink) {
        this.downloadLink = downloadLink;
    }

    public LocalDateTime getGeneratedTime() {
        return generatedTime;
    }

    public void setGeneratedTime(LocalDateTime generatedTime) {
        this.generatedTime = generatedTime;
    }
}
