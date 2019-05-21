package com.scheduler;

import java.io.IOException;

import org.apache.poi.EncryptedDocumentException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.util.ExcelUtil;

@Component
public class ScheduledTasks {

	@Autowired
	ExcelUtil util;

	@Value("${output.excel.file}")
	String filename;

	@Scheduled(cron = "${cron.expression}")
	public void processBatchReport() throws EncryptedDocumentException, IOException {

		util.updateData(filename);

	}

}
