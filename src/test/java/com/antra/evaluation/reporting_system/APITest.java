package com.antra.evaluation.reporting_system;

import com.antra.evaluation.reporting_system.endpoint.ExcelGenerationController;
import com.antra.evaluation.reporting_system.pojo.report.ExcelFile;
import com.antra.evaluation.reporting_system.service.ExcelService;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.hasItems;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
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
    public void testCreateExcel() throws IOException {
        // Mockito.when(excelService.getExcelBodyById(anyString())).thenReturn(new FileInputStream("temp.xlsx"));
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "test.xlsx";
        Mockito.when(excelService.getNextId()).thenReturn("1");
        Mockito.when(excelService.generateExcelReport(any())).thenReturn(new File(fileLocation));
        Mockito.when(excelService.saveExcelFileInfo(any())).thenReturn(null);
        given().accept("application/json").contentType(ContentType.JSON).body("{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]]}").post("/excel").peek().
                then().assertThat()
                .statusCode(200)
                .body("file.fileId", Matchers.notNullValue());
    }

    @Test
    public void testCreateMultiExcel() throws IOException {
        File currDir = new File(".");
        String path = currDir.getAbsolutePath();
        String fileLocation = path.substring(0, path.length() - 1) + "test.xlsx";
        Mockito.when(excelService.getNextId()).thenReturn("1");
        Mockito.when(excelService.generateExcelReport(any())).thenReturn(new File(fileLocation));
        Mockito.when(excelService.saveExcelFileInfo(any())).thenReturn(null);
        given().accept("application/json").contentType(ContentType.JSON).body("{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]],\"splitBy\":\"Age\"}").post("/excel/auto").peek().
                then().assertThat()
                .statusCode(200)
                .body("file.numOfSheets", equalTo(2));
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
