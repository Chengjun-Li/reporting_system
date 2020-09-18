package com.antra.evaluation.reporting_system.endpoint;


import com.antra.evaluation.reporting_system.exception.ExcelFileNotFoundException;
import com.antra.evaluation.reporting_system.exception.IllegalRequestParametersException;
import com.antra.evaluation.reporting_system.pojo.api.*;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.service.ExcelService;
import io.swagger.annotations.ApiOperation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;


import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.stream.Collectors;


@RestController
public class ExcelGenerationController {

    private static final Logger logger = LoggerFactory.getLogger(ExcelGenerationController.class);

    ExcelService excelService;

    @Autowired
    public ExcelGenerationController(ExcelService excelService) {
        this.excelService = excelService;
    }

    @PostMapping("/excel")
    @ApiOperation("Generate Excel")
    public ResponseEntity<ExcelResponse> createExcel(@RequestBody @Validated ExcelRequest request) throws IOException {
        ExcelResponse response = excelService.createExcel(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @PostMapping("/excel/all")
    @ApiOperation("Generate Multiple Excels")
    public ResponseEntity<List<ExcelResponse>> createMultipleExcels(@RequestBody @Validated List<ExcelRequest> requestList) throws IOException {
        List<ExcelResponse> list = excelService.createMultipleExcels(requestList);
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/excel/auto")
    @ApiOperation("Generate Multi-Sheet Excel Using Split field")
    public ResponseEntity<ExcelResponse> createMultiSheetExcel(@RequestBody @Validated MultiSheetExcelRequest request) throws IOException {
        ExcelResponse response = excelService.createMultiSheetExcel(request);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/excel")
    @ApiOperation("List all existing files")
    public ResponseEntity<List<ExcelResponse>> listExcels() {
        List<ExcelFile> files = excelService.getFiles();
        List<ExcelResponse> response = files.stream().
                map(file -> {
                    ExcelResponse resp = new ExcelResponse();
                    resp.setFile(file);
                    return resp;
                })
                .collect(Collectors.toList());
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    @GetMapping("/excel/{id:\\d+}/content")
    @ApiOperation("Download a single excel")
    public void downloadExcel(@PathVariable String id, HttpServletResponse response) throws IOException {
        InputStream fis = excelService.getExcelBodyById(id);
        response.setHeader("Content-Type", "application/vnd.ms-excel");
        response.setHeader("Content-Disposition", "attachment; filename=\"" + id + ".xlsx\""); // TODO: File name cannot be hardcoded here
        FileCopyUtils.copy(fis, response.getOutputStream());
    }

    @PostMapping("/excel/all/content")
    @ApiOperation("Download multiple excels as a zip file")
    public void downloadZipFile(@RequestBody List<ExcelFileId> fileIds, HttpServletResponse response) throws IOException {
        excelService.downloadZipFile(fileIds,response);
    }

    @DeleteMapping("/excel/{id:\\d+}")
    @ApiOperation("Delete a specific excel file")
    public ResponseEntity<ResponseMessage> deleteExcel(@PathVariable String id) {
        ExcelFile excelFile = excelService.getExcelFileById(id);
        if (excelFile == null) {
            throw new ExcelFileNotFoundException(id);
        }
        excelService.deleteExcel(excelFile);
        var response = new ExcelResponse();
        response.setFile(excelFile);
        return new ResponseEntity<>(new ResponseMessage("File " + id + ".xlsx successfully deleted", response), HttpStatus.OK);
    }

    @ExceptionHandler(ExcelFileNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleExcelFileNotFoundException(ExcelFileNotFoundException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setErrorCode(HttpStatus.NOT_FOUND.value());
        error.setMessage(ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleMethodArgumentNotValidException(MethodArgumentNotValidException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setErrorCode(HttpStatus.BAD_REQUEST.value());
        error.setMessage("Bad Request Error: request body is not valid");
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<ErrorResponse> handleIllegalRequestParametersException(IllegalRequestParametersException ex) {
        ErrorResponse error = new ErrorResponse();
        error.setErrorCode(HttpStatus.BAD_REQUEST.value());
        error.setMessage(ex.getMessage());
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleException(Exception ex) {
        ErrorResponse error = new ErrorResponse();
        error.setErrorCode(HttpStatus.INTERNAL_SERVER_ERROR.value());
        error.setMessage(ex.getMessage());
        logger.error("Controller Error", ex);
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

}
// Log
// Exception handling
// Validation
