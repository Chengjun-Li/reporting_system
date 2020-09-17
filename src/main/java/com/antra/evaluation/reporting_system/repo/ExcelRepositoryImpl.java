package com.antra.evaluation.reporting_system.repo;

import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;
import java.util.stream.Collectors;

@Repository
public class ExcelRepositoryImpl implements ExcelRepository {

    private AtomicLong id = new AtomicLong(0);

    Map<String, ExcelFile> excelData = new ConcurrentHashMap<>();

    @Override
    public Optional<ExcelFile> getFileById(String id) {
        return Optional.ofNullable(excelData.get(id));
    }

    @Override
    public ExcelFile saveFile(ExcelFile file) {
        excelData.put(file.getFileId(), file);
        return file;
    }

    @Override
    public List<ExcelFile> getFiles() {
        return excelData.values().stream().collect(Collectors.toList());
    }

    @Override
    public String getNextId(){
        return String.valueOf(id.getAndAdd(1));
    }

    @Override
    public void deleteFileById(String id) {
        excelData.remove(id);
    }
}

