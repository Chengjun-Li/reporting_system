package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.exception.ExcelFileNotFoundException;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.report.ExcelData;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.Optional;

@Service
public class ExcelServiceImpl implements ExcelService {

    @Autowired
    ExcelRepository excelRepository;

    @Autowired
    ExcelGenerationService excelGenerationService;

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
    public ExcelFile getExcelFileById(String id) {
        Optional<ExcelFile> fileInfo = excelRepository.getFileById(id);
        if (fileInfo.isPresent()){
            return fileInfo.get();
        } else {
            throw new ExcelFileNotFoundException(id);
        }
    }

    @Override
    public File generateExcelReport(ExcelData data) throws IOException {
        return excelGenerationService.generateExcelReport(data);
    }

    @Override
    public ExcelFile saveExcelFileInfo(ExcelFile file){
        return excelRepository.saveFile(file);
    }

    @Override
    public String getNextId(){
        return excelRepository.getNextId();
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
}
