package com.antra.evaluation.reporting_system.Util;

import com.antra.evaluation.reporting_system.exception.IllegalRequestParametersException;
import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataHeader;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataSheet;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;

import java.io.File;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Converter {

    public static ExcelData convertRequestToExcelData(ExcelRequest request){
        List<ExcelDataHeader> headers = convertStringsToHeaders(request.getHeaders());
        ExcelDataSheet singleSheet = new ExcelDataSheet("Sheet1", headers, request.getData());
        List<ExcelDataSheet> sheets = new ArrayList<>();
        sheets.add(singleSheet);
        ExcelData data = buildExcelData(sheets);
        return data;
    }

    public static ExcelData convertMultiSheetRequestToExcelData(MultiSheetExcelRequest request){
        String splitBy = request.getSplitBy();
        //get index of splitBy parameter in headers
        int index = request.getHeaders().indexOf(splitBy);
        if (index == -1) {
            throw new IllegalRequestParametersException("there is no header with name: " + splitBy);
        }
        //build a map, key is distinct value in splitBy column, value is list of row data
        Map<String, List<List<String>>> map = buildMap(request.getData(), index);

        List<ExcelDataHeader> headers = convertStringsToHeaders(request.getHeaders());
        List<ExcelDataSheet> sheets = new ArrayList<>();
        for (Map.Entry<String, List<List<String>>> entry : map.entrySet()) {
            ExcelDataSheet sheet = new ExcelDataSheet(entry.getKey(), headers, entry.getValue());
            sheets.add(sheet);
        }
        ExcelData data = buildExcelData(sheets);
        return data;
    }

    public static ExcelResponse convertExcelFileToResponse(ExcelFile file){
        ExcelResponse excelResponse = new ExcelResponse();
        excelResponse.setFile(file);
        return excelResponse;
    }



    private static List<ExcelDataHeader> convertStringsToHeaders(List<String> list) {
        List<ExcelDataHeader> headers = list.stream().map(string -> {
            ExcelDataHeader header = new ExcelDataHeader();
            header.setName(string);
            header.setWidth(5);
            return header;
        }).collect(Collectors.toList());
        return headers;
    }

    private static ExcelData buildExcelData(List<ExcelDataSheet> sheets) {
        ExcelData data = new ExcelData();
        data.setGeneratedTime(LocalDateTime.now());
        data.setSheets(sheets);
        return data;
    }

    private static Map<String, List<List<String>>> buildMap(List<List<String>> data, int index) {
        Map<String, List<List<String>>> map = new HashMap<>();
        for (List<String> row : data) {
            String splitByValue = row.get(index);
            List<List<String>> rows = map.getOrDefault(splitByValue, new ArrayList<>());
            rows.add(row);
            map.put(splitByValue, rows);
        }
        return map;
    }
}
