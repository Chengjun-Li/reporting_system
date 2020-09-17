package com.antra.evaluation.reporting_system.exception;

public class ExcelFileNotFoundException extends RuntimeException{
    private String fileId;
    private String message;

    public ExcelFileNotFoundException(String fileId) {
        super();
        this.fileId = fileId;
        this.message = "Excel File with id = " + this.fileId + " not found";
    }
    public String getFileId() {
        return fileId;
    }

    public void setFileId(String fileId) {
        this.fileId = fileId;
    }

    @Override
    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}
