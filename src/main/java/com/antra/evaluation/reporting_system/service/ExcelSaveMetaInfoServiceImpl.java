package com.antra.evaluation.reporting_system.service;

import com.antra.evaluation.reporting_system.Util.Converter;
import com.antra.evaluation.reporting_system.pojo.api.ExcelResponse;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.repo.ExcelRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class ExcelSaveMetaInfoServiceImpl implements ExcelSaveMetaInfoService {

    @Autowired
    ExcelRepository excelRepository;

    @Override
    public ExcelResponse saveMetaInfo(ExcelFile File) {
        ExcelFile excelFile = excelRepository.saveFile(File);
        return Converter.convertExcelFileToResponse(excelFile);
    }
}
