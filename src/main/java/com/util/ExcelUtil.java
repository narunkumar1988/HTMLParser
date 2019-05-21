package com.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.model.JobDuration;
import com.util.constants.ParserConstants;

import io.micrometer.core.instrument.util.StringUtils;

@Service
public class ExcelUtil {
	
	Logger log = LoggerFactory.getLogger(ExcelUtil.class);
	
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
		Workbook wb = WorkbookFactory.create(inp);
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
		
		String pattern = ParserConstants.DATE_PATTERN_MINIMAL;
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);

		String date = simpleDateFormat.format(new Date());
		log.info(date);
		
		Workbook wb = new XSSFWorkbook(new FileInputStream(new File(fileName)));

		Sheet sheet = wb.getSheet(sheetName);
		log.info(file);
		
		sheet.getRow(3).getCell(1).setCellValue(date);

		IntStream.range(6, rowsToBeProcessed).forEach((rowNum) -> {
			Row row = sheet.getRow(rowNum);
			if (row != null) {
				Cell jobRow = row.getCell(jobRowIndex);
				System.out.println(jobRow+":");
				if(jobRow!=null) {
					String jobName = jobRow.getStringCellValue();
					if (StringUtils.isNotBlank(jobName) && jobMap.containsKey(jobName)) {
						JobDuration duration = jobMap.get(jobName);
						row.getCell(startTimeIndex).setCellValue(duration.getActualStart());
						row.getCell(durationIndex).setCellValue(duration.getActualDuration());
					}
				}	
			}
		});

		FileOutputStream out = new FileOutputStream(fileName);
		wb.write(out);
		out.close();
		isShifted =  true;
		// Closing the workbook
		wb.close();
		return isShifted;
	}


}
