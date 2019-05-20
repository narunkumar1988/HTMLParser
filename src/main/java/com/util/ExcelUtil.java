package com.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.stream.IntStream;

import org.apache.poi.EncryptedDocumentException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.ss.util.CellRangeAddress;
import org.apache.poi.xssf.streaming.SXSSFWorkbook;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.model.JobDuration;

@Service
public class ExcelUtil {
	
	@Autowired
	HtmlParserUtil parser;
	
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
	
	public boolean createExcel(String fileName) throws IOException {
		return this.generateExcel(parser.prepareDataForExcel(fileName));
	}

	private boolean generateExcel(List<List<String>> jobs) throws IOException {
		SXSSFWorkbook workbook = new SXSSFWorkbook();
		Sheet sheet = workbook.createSheet("Jobs");
		IntStream.range(0, jobs.size()).forEach((rowNum) -> {
			List<String> job = jobs.get(rowNum);
			Row row = sheet.createRow(rowNum);
			IntStream.range(0, job.size()).forEach((col) -> {
				Cell cell = row.createCell(col);
				cell.setCellValue(job.get(col));
			});
		});

		FileOutputStream outputStream = new FileOutputStream(file);
		workbook.write(outputStream);
		outputStream.close();
		workbook.dispose();
		workbook.close();
		return true;
	}

	public boolean shiftColumns(String fileName) throws IOException {
		boolean isShifted = false;
		InputStream inp = new FileInputStream(new File(fileName));
		// SXSSFWorkbook wb = new SXSSFWorkbook();
		Workbook wb = WorkbookFactory.create(inp);
		// Sheet sheet = wb.getSheet("CopyJob");
		XSSFSheet sheet = (XSSFSheet) wb.getSheet("CopyJob");
		sheet.shiftColumns(7, 12, -2);
		OutputStream os = new FileOutputStream(fileName);
		wb.write(os);
		wb.close();
		CellRangeAddress cell = new CellRangeAddress(1, 2, 1, 2);
		cell.copy();
		return isShifted;
	}
	
	public boolean updateData(String fileName) throws EncryptedDocumentException, IOException {
		return updateData(fileName, parser.parseDetails(htmlFile));
	}

	public boolean updateData(String fileName, Map<String, JobDuration> jobMap)
			throws EncryptedDocumentException, IOException {
		boolean isShifted = false;
		Workbook wb = new XSSFWorkbook(new FileInputStream(new File(fileName)));

		Sheet sheet = wb.getSheet(sheetName);
		System.out.println(file);

		IntStream.range(1, rowsToBeProcessed).forEach((rowNum) -> {
			Row row = sheet.getRow(rowNum);
			if (row != null) {
				System.out.println(row.getCell(jobRowIndex));
				String jobName = row.getCell(jobRowIndex).getStringCellValue();
				if (jobName != null && jobMap.containsKey(jobName)) {
					JobDuration duration = jobMap.get(jobName);
					row.getCell(startTimeIndex).setCellValue(duration.getActualStart());
					row.getCell(durationIndex).setCellValue(duration.getActualDuration());
				}
			}
		});

		FileOutputStream out = new FileOutputStream(fileName);
		wb.write(out);
		out.close();

		// Closing the workbook
		wb.close();
		return isShifted;
	}


}
