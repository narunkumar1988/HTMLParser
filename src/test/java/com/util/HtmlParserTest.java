package com.util;

import static org.junit.Assert.assertNotNull;

import java.io.IOException;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.util.ReflectionTestUtils;

import com.model.JobDetail;

@RunWith(SpringRunner.class)
@WebAppConfiguration
@SpringBootTest
public class HtmlParserTest {
	
	
	@Value("${input.html.tableId}")
	String tableElementId;
	
	@Value("${input.html.file}")
	String htmlFile;
	
	HtmlParserUtil util = new HtmlParserUtil();
	
	@Before
	public void setup() {
		ReflectionTestUtils.setField(util, "tableElementId", tableElementId); 
	}
	
	@Test
	public void parseHTMLTest() throws IOException {
		List<JobDetail> data = util.parseHTML(htmlFile);
		assertNotNull(data);
	}
	

}
