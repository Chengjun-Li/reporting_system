package com.antra.evaluation.reporting_system;

import com.antra.evaluation.reporting_system.pojo.report.*;
import com.antra.evaluation.reporting_system.service.ExcelGenerationService;
import com.antra.evaluation.reporting_system.service.ExcelService;
import io.restassured.http.ContentType;
import io.restassured.module.mockmvc.RestAssuredMockMvc;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.web.context.WebApplicationContext;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static io.restassured.module.mockmvc.RestAssuredMockMvc.given;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
class ReportingSystemApplicationTests {

    @Autowired
    ExcelGenerationService reportService;

    ExcelData data = new ExcelData();

    @Autowired
    WebApplicationContext wac;

    @Autowired
    ExcelService excelService;

    @BeforeEach // We are using JUnit 5, @Before is replaced by @BeforeEach
    public void setUpData() {
        RestAssuredMockMvc.webAppContextSetup(wac);

        data.setTitle("Test book");
        data.setGeneratedTime(LocalDateTime.now());

        var sheets = new ArrayList<ExcelDataSheet>();
        var sheet1 = new ExcelDataSheet();
        sheet1.setTitle("First Sheet");

        var headersS1 = new ArrayList<ExcelDataHeader>();
        ExcelDataHeader header1 = new ExcelDataHeader();
        header1.setName("NameTest");
        //       header1.setWidth(10000);
        header1.setType(ExcelDataType.STRING);
        headersS1.add(header1);

        ExcelDataHeader header2 = new ExcelDataHeader();
        header2.setName("Age");
        //   header2.setWidth(10000);
        header2.setType(ExcelDataType.NUMBER);
        headersS1.add(header2);

        List<List<String>> dataRows = new ArrayList<>();
        List<String> row1 = new ArrayList<>();
        row1.add("Dawei");
        row1.add("12");
        List<String> row2 = new ArrayList<>();
        row2.add("Dawei2");
        row2.add("23");
        dataRows.add(row1);
        dataRows.add(row2);

        sheet1.setDataRows(dataRows);
        sheet1.setHeaders(headersS1);
        sheets.add(sheet1);
        data.setSheets(sheets);

        var sheet2 = new ExcelDataSheet();
        sheet2.setTitle("second Sheet");
        sheet2.setDataRows(dataRows);
        sheet2.setHeaders(headersS1);
        sheets.add(sheet2);
    }

    @Test
    public void testExcelGeneration() {
        File file = null;
        try {
            file = reportService.generateExcelReport(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
        assertTrue(file != null);
    }

    @Test
    public void testCreateExcel(){
        given().accept("application/json").contentType(ContentType.JSON).body("{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]]}").post("/excel").peek().
                then().assertThat()
                .statusCode(200)
                .body("file.fileId", Matchers.notNullValue());
    }

    @Test
    public void testCreateMultiExcel() throws IOException {
        given().accept("application/json").contentType(ContentType.JSON).body("{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]],\"splitBy\":\"Age\"}").post("/excel/auto").peek().
                then().assertThat()
                .statusCode(200)
                .body("file.numOfSheets",Matchers.equalTo(2));
    }

    @Test
    public void testCreateMultipleExcel(){
        given().accept("application/json").contentType(ContentType.JSON).body("[{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]]},{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]]}]").post("/excel/all").peek().
                then().assertThat()
                .statusCode(200)
                .body("file.fileId", Matchers.iterableWithSize(2));
    }


    @Test
    public void testDeleteExcelWhenFileExist(){
        String fileId = given().accept("application/json").contentType(ContentType.JSON).body("{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]]}").post("/excel").peek().
                then().assertThat()
                .statusCode(200)
                .body("file.fileId", Matchers.notNullValue())
                .extract().path("file.fileId");

        given().accept("application/json").delete("/excel/" + fileId).peek().
                then().assertThat()
                .statusCode(200);
    }
    @Test
    public void testDeleteExcelWhenFileNotExist(){
        given().accept("application/json").delete("/excel/-1").peek().
                then().assertThat()
                .statusCode(404);
    }

    @Test
    public void testListExcelsWhenFilesExist(){
        String fileId_1 = given().accept("application/json").contentType(ContentType.JSON).body("{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]]}").post("/excel").peek().
                then().assertThat()
                .statusCode(200)
                .body("file.fileId", Matchers.notNullValue())
                .extract().path("file.fileId");

        String fileId_2 = given().accept("application/json").contentType(ContentType.JSON).body("{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]]}").post("/excel").peek().
                then().assertThat()
                .statusCode(200)
                .body("file.fileId", Matchers.notNullValue())
                .extract().path("file.fileId");

        given().accept("application/json").get("/excel").peek().
                then().assertThat()
                .statusCode(200)
                .body("file.fileId", Matchers.hasItems(fileId_1,fileId_2));
    }

    @Test
    public void testDownloadFileWhenFileExist(){
        String fileId = given().accept("application/json").contentType(ContentType.JSON).body("{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]]}").post("/excel").peek().
                then().extract().path("file.fileId");

        given().accept("application/json").get("/excel/" + fileId + "/content").peek().
                then().assertThat()
                .statusCode(200);
    }

    @Test
    public void testDownloadZipFileWhenFileExist(){
        String fileId_1 = given().accept("application/json").contentType(ContentType.JSON).body("{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]]}").post("/excel").peek().
                then().extract().path("file.fileId");

        String fileId_2 = given().accept("application/json").contentType(ContentType.JSON).body("{\"headers\":[\"Name\",\"Age\"], \"data\":[[\"Teresa\",\"5\"],[\"Daniel\",\"1\"]]}").post("/excel").peek().
                then().extract().path("file.fileId");

        given().accept("application/json").contentType(ContentType.JSON).body("[{\"fileId\": " + fileId_1 + "},{\"fileId\": " + fileId_2 + "}]").post("/excel/all/content").peek().
                then().assertThat()
                .statusCode(200);
    }

    @AfterEach
    public void clearCreatedExcelFiles(){
        List<String> fileIds = given().accept("application/json").get("/excel").peek().
                then().extract().path("file.fileId");
        for(String fileId : fileIds){
            given().accept("application/json").delete("/excel/" + fileId).peek();
        }
    }
}
