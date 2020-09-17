package com.antra.evaluation.reporting_system.endpoint;


import com.antra.evaluation.reporting_system.exception.ExcelFileNotFoundException;
import com.antra.evaluation.reporting_system.exception.IllegalRequestParametersException;
import com.antra.evaluation.reporting_system.pojo.api.*;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataHeader;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataSheet;
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
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

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
        //Convert ExcelRequest to ExcelData
        List<ExcelDataHeader> headers = convertStringsToHeaders(request.getHeaders());
        ExcelDataSheet singleSheet = new ExcelDataSheet("Sheet1", headers, request.getData());
        List<ExcelDataSheet> sheets = new ArrayList<>();
        sheets.add(singleSheet);
        ExcelData data = buildExcelData(sheets);
        //generate excel report
        File file = excelService.generateExcelReport(data);
        //save excel meta data
        ExcelFile excelFile = buildExcelFile(request, data, file);
        excelService.saveExcelFileInfo(excelFile);
        //return user excel meta data
        ExcelResponse response = new ExcelResponse();
        response.setFile(excelFile);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private List<ExcelDataHeader> convertStringsToHeaders(List<String> list) {
        List<ExcelDataHeader> headers = list.stream().map(string -> {
            ExcelDataHeader header = new ExcelDataHeader();
            header.setName(string);
            header.setWidth(5);
            return header;
        }).collect(Collectors.toList());
        return headers;
    }

    private ExcelData buildExcelData(List<ExcelDataSheet> sheets) {
        ExcelData data = new ExcelData();
        data.setGeneratedTime(LocalDateTime.now());
        data.setSheets(sheets);
        String fileId = excelService.getNextId();
        data.setTitle(fileId);
        return data;
    }

    private ExcelFile buildExcelFile(ExcelRequest request, ExcelData data, File file) {
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

    @PostMapping("/excel/all")
    @ApiOperation("Generate Multiple Excels")
    public ResponseEntity<List<ExcelResponse>> createMultipleExcels(@RequestBody @Validated List<ExcelRequest> requestList) throws IOException {
        //Convert ExcelRequest to ExcelData
        List<ExcelResponse> list = new ArrayList<>();
        for (ExcelRequest request : requestList) {
            List<ExcelDataHeader> headers = convertStringsToHeaders(request.getHeaders());
            ExcelDataSheet singleSheet = new ExcelDataSheet("Sheet1", headers, request.getData());
            List<ExcelDataSheet> sheets = new ArrayList<>();
            sheets.add(singleSheet);
            ExcelData data = buildExcelData(sheets);
            //generate excel report
            File file = excelService.generateExcelReport(data);
            //save excel meta data
            ExcelFile excelFile = buildExcelFile(request, data, file);
            excelService.saveExcelFileInfo(excelFile);
            //return user excel meta data
            ExcelResponse response = new ExcelResponse();
            response.setFile(excelFile);
            list.add(response);
        }
        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    @PostMapping("/excel/auto")
    @ApiOperation("Generate Multi-Sheet Excel Using Split field")
    public ResponseEntity<ExcelResponse> createMultiSheetExcel(@RequestBody @Validated MultiSheetExcelRequest request) throws IOException {
        String splitBy = request.getSplitBy();
        //get index of splitBy parameter in headers
        int index = request.getHeaders().indexOf(splitBy);
        if (index == -1) {
            throw new IllegalRequestParametersException("there is no header with name: " + splitBy);
        }
        //build a map with key is each distinct value in index column, value is list of row data
        Map<String, List<List<String>>> map = buildMap(request.getData(), index);

        List<ExcelDataHeader> headers = convertStringsToHeaders(request.getHeaders());
        List<ExcelDataSheet> sheets = new ArrayList<>();
        for (Map.Entry<String, List<List<String>>> entry : map.entrySet()) {
            ExcelDataSheet sheet = new ExcelDataSheet(entry.getKey(), headers, entry.getValue());
            sheets.add(sheet);
        }
        ExcelData data = buildExcelData(sheets);
        //generate excel file
        File file = excelService.generateExcelReport(data);
        //save excel meta data
        ExcelFile excelFile = buildExcelFile(request, data, file);
        excelService.saveExcelFileInfo(excelFile);
        //return user excel meta data
        ExcelResponse response = new ExcelResponse();
        response.setFile(excelFile);
        return new ResponseEntity<>(response, HttpStatus.OK);
    }

    private Map<String, List<List<String>>> buildMap(List<List<String>> data, int index) {
        Map<String, List<List<String>>> map = new HashMap<>();
        for (List<String> row : data) {
            String splitByValue = row.get(index);
            List<List<String>> rows = map.getOrDefault(splitByValue, new ArrayList<>());
            rows.add(row);
            map.put(splitByValue, rows);
        }
        return map;
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
        String zipName = "ExcelFile.zip";
        response.setContentType("APPLICATION/OCTET-STREAM");
        response.setHeader("Content-Disposition","attachment; filename="+zipName);
        // create byte buffer
        byte[] buffer = new byte[1024];
        OutputStream fos = response.getOutputStream();
        ZipOutputStream zos = new ZipOutputStream(fos);
        for (int i = 0; i < fileIds.size(); i++) {
            InputStream fis = excelService.getExcelBodyById(fileIds.get(i).getFileId());
            // begin writing a new ZIP entry, positions the stream to the start of the entry data
            zos.putNextEntry(new ZipEntry(fileIds.get(i).getFileId() + ".xlsx"));
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
            // close the InputStream
            fis.close();
        }
        // close the ZipOutputStream
        zos.close();
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
