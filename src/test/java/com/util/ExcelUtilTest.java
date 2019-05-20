package com.util;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.io.File;
import java.io.IOException;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

@RunWith(SpringRunner.class)
@SpringBootTest
@WebAppConfiguration
public class ExcelUtilTest {
	
	@Value("${output.excel.index.job}")
	int jobRowIndex;
	
	@Value("${output.excel.index.startTime}")
	int startTimeIndex;
	
	@Value("${output.excel.index.duration}")
	int durationIndex;

	@Value("${output.excel.file}")
	String file;
	
	@Value("${output.excel.sheetName}")
	String sheetName;
	
	@Value("${output.excel.rowsToBeProcessed}")
	int rowsToBeProcessed;
	
	@Value("${input.html.file}")
	String htmlFile;
	
	@Value("${input.html.tableId}")
	String tableElementId;
	
	HtmlParserUtil parser = new HtmlParserUtil();
	
	@Autowired
	ExcelUtil util;
	

	
	@Before
	public void setup() {
		ReflectionTestUtils.setField(util, "jobRowIndex", jobRowIndex);
		ReflectionTestUtils.setField(util, "startTimeIndex", startTimeIndex);
		ReflectionTestUtils.setField(util, "durationIndex", durationIndex);
		ReflectionTestUtils.setField(util, "file", file);
		ReflectionTestUtils.setField(util, "sheetName", sheetName);
		ReflectionTestUtils.setField(util, "htmlFile", htmlFile);
		ReflectionTestUtils.setField(util, "rowsToBeProcessed", rowsToBeProcessed);
		ReflectionTestUtils.setField(util, "parser", parser);
		ReflectionTestUtils.setField(parser, "tableElementId", tableElementId);
	}
	
	

	//@Test
	public void createExcel() throws IOException {
		util.createExcel(htmlFile);
		File excel = new File(file);
		assertNotNull(excel);
		assertTrue(excel.exists());
		//util.shiftColumns("E:\\Arun\\supportPOC\\HTMLParser\\sxssf_example.xlsx");
	}
	
	@Test
	public void updateDataTest() throws IOException {
		assertTrue(util.updateData(file));
	}
	

}
