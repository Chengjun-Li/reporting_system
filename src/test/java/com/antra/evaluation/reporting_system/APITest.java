package com.antra.evaluation.reporting_system;

import com.antra.evaluation.reporting_system.endpoint.ExcelGenerationController;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.service.ExcelService;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static org.hamcrest.Matchers.hasItems;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;

public class APITest {
    @Mock
    ExcelService excelService;

    @BeforeEach
    public void configMock() {
        MockitoAnnotations.initMocks(this);
        RestAssuredMockMvc.standaloneSetup(new ExcelGenerationController(excelService));
    }

    @Test
    public void testValidUrlFileDownload() throws FileNotFoundException {
        Mockito.when(excelService.getExcelBodyById(anyString())).thenReturn(new FileInputStream("test.xlsx"));
        given().accept("application/json").get("/excel/123/content").peek().
                then().assertThat()
                .statusCode(200);
    }

    @Test
    public void testInvalidUrlFileDownload() throws FileNotFoundException {
        Mockito.when(excelService.getExcelBodyById(anyString())).thenReturn(new FileInputStream("test.xlsx"));
        given().accept("application/json").get("/excel/123abcd/content").peek().
                then().assertThat()
                .statusCode(404);
    }

    @Test
    public void testListFiles() throws FileNotFoundException {
        List<ExcelFile> excelFiles = new ArrayList<>();
        ExcelFile dummyFile1 = new ExcelFile();
        dummyFile1.setFileId(String.valueOf(1));
        ExcelFile dummyFile2 = new ExcelFile();
        dummyFile2.setFileId(String.valueOf(2));
        excelFiles.add(dummyFile1);
        excelFiles.add(dummyFile2);
        Mockito.when(excelService.getFiles()).thenReturn(excelFiles);
        given().accept("application/json").get("/excel").peek().
                then().assertThat()
                .statusCode(200)
                .body("file.fileId",hasItems(String.valueOf(1),String.valueOf(2)));

    }

    @Test
    public void testDeleteExcel(){
        ExcelFile file = new ExcelFile();
        Mockito.when(excelService.getExcelFileById(anyString())).thenReturn(file);
        Mockito.doNothing().when(excelService).deleteExcel(any(ExcelFile.class));
        given().accept("application/json").delete("/excel/1").peek().
                then().assertThat()
                .statusCode(200);
    }
}
