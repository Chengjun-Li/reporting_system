package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.Util.Converter;
import com.antra.evaluation.reporting_system.Util.Utility;
import com.antra.evaluation.reporting_system.exception.ExcelFileNotFoundException;
import com.antra.evaluation.reporting_system.pojo.api.ExcelFileId;
import com.antra.evaluation.reporting_system.pojo.api.ExcelRequest;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.api.MultiSheetExcelRequest;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataHeader;
import com.antra.evaluation.reporting_system.pojo.report.ExcelDataSheet;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    ExcelRepository excelRepository;

    @Autowired
    ExcelGenerationService excelGenerationService;

    @Autowired
    ExcelSaveMetaInfoService excelSaveMetaInfoService;

    @Override
    public InputStream getExcelBodyById(String id) throws FileNotFoundException {

        Optional<ExcelFile> fileInfo = excelRepository.getFileById(id);
        if (fileInfo.isPresent()) {
            String fileId = fileInfo.get().getFileId();
            File file = new File(fileId + ".xlsx");
            try {
                return new FileInputStream(file);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
                throw e;
            }
        } else {
            throw new ExcelFileNotFoundException(id);
        }
    }

    @Override
    public void downloadZipFile(List<ExcelFileId> fileIds, HttpServletResponse response) throws IOException {
        String zipName = "ExcelFile.zip";
        response.setContentType("APPLICATION/OCTET-STREAM");
        response.setHeader("Content-Disposition","attachment; filename="+zipName);
        byte[] buffer = new byte[1024];
        OutputStream fos = response.getOutputStream();
        ZipOutputStream zos = new ZipOutputStream(fos);
        for (int i = 0; i < fileIds.size(); i++) {
            InputStream fis = getExcelBodyById(fileIds.get(i).getFileId());
            zos.putNextEntry(new ZipEntry(fileIds.get(i).getFileId() + ".xlsx"));
            int length;
            while ((length = fis.read(buffer)) > 0) {
                zos.write(buffer, 0, length);
            }
            zos.closeEntry();
            fis.close();
        }
        zos.close();
    }

    @Override
    public ExcelFile getExcelFileById(String id) {
        Optional<ExcelFile> fileInfo = excelRepository.getFileById(id);
        if (fileInfo.isPresent()){
            return fileInfo.get();
        } else {
            throw new ExcelFileNotFoundException(id);
        }
    }

    @Override
    public void deleteExcel(ExcelFile excelFile) {
        excelRepository.deleteFileById(excelFile.getFileId());
        File file = new File(excelFile.getFileLocation());
        file.delete();
    }

    @Override
    public List<ExcelFile> getFiles() {
        return excelRepository.getFiles();
    }

    @Override
    public ExcelResponse createExcel(ExcelRequest request) throws IOException {
        ExcelData data = Converter.convertRequestToExcelData(request);
        data.setTitle(excelRepository.getNextId()); //use id as title, in order to make generated .xlsx file has an unique name.
        File file = excelGenerationService.generateExcelReport(data);
        ExcelFile excelFile = Utility.buildExcelFile(request, data, file);
        ExcelResponse excelResponse = excelSaveMetaInfoService.saveMetaInfo(excelFile);
        return excelResponse;
    }

    @Override
    public ExcelResponse createMultiSheetExcel(MultiSheetExcelRequest request) throws IOException {
        ExcelData data = Converter.convertMultiSheetRequestToExcelData(request);
        data.setTitle(excelRepository.getNextId());
        File file = excelGenerationService.generateExcelReport(data);
        ExcelFile excelFile = Utility.buildExcelFile(request, data, file);
        ExcelResponse excelResponse = excelSaveMetaInfoService.saveMetaInfo(excelFile);
        return excelResponse;
    }

    @Override
    public List<ExcelResponse> createMultipleExcels(List<ExcelRequest> requestList) throws IOException {
        List<ExcelResponse> list = new ArrayList<>();
        for (ExcelRequest request : requestList) {
            ExcelData data = Converter.convertRequestToExcelData(request);
            data.setTitle(excelRepository.getNextId()); //use id as title, in order to make generated .xlsx file has an unique name.
            File file = excelGenerationService.generateExcelReport(data);
            ExcelFile excelFile = Utility.buildExcelFile(request, data, file);
            ExcelResponse excelResponse = excelSaveMetaInfoService.saveMetaInfo(excelFile);
            list.add(excelResponse);
        }
        return list;
    }
}
