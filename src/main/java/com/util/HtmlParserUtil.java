package com.util;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections4.CollectionUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.model.JobDetail;
import com.model.JobDuration;
import com.util.constants.ParserConstants;

@Service
public class HtmlParserUtil {
	
	@Value("${input.html.tableId}")
	String tableElementId;
	

	/*
	 * public Map<String, JobDuration> parseDetails(String fileName) throws
	 * IOException { Document doc = Jsoup.parse(new File(fileName), "utf-8");
	 * Element tableTag = doc.getElementById("__bookmark_1"); Map<String,
	 * JobDuration> jobMap = new HashMap<>(); for (Element elem1 :
	 * tableTag.children()) { if ("tbody".equals(elem1.tagName())) { for (Element
	 * elemtr : elem1.children()) { if (elemtr.child(0).childNodeSize() > 0) {
	 * JobDuration job = new JobDuration();
	 * job.setJob(elemtr.child(1).child(0).text());
	 * job.setActualStart(elemtr.child(5).child(0).text());
	 * job.setActualEnd(elemtr.child(6).child(0).text());
	 * job.setActualDuration(elemtr.child(7).child(0).text()); if
	 * (!jobMap.containsKey(job.getJob())) jobMap.put(job.getJob(), job); } } } }
	 * return jobMap; }
	 */

	public Map<String, JobDuration> parseDetails(String fileName) throws IOException {
		Map<String, JobDuration> jobMap = null;
		List<JobDetail> jobs = parseHTML(fileName);
		if (CollectionUtils.isNotEmpty(jobs)) {
			jobMap = jobs.stream().filter(p -> p.getJob() != null)
					.collect(Collectors.toMap(JobDetail::getJob, JobDetail::creationJobDuration));
		}
		return jobMap;
	}

	public List<JobDetail> parseHTML(String fileName) throws IOException {
		Document doc = Jsoup.parse(new File(fileName), ParserConstants.FILE_ENC_UTF8);
		Element tableTag = doc.getElementById(tableElementId);
		List<JobDetail> jobs = new ArrayList<>();
		Set<String> jobNames = new HashSet<>();
		for (Element elem1 : tableTag.children()) {
			if (ParserConstants.TAG_TABLE_BODY.equals(elem1.tagName())) {
				for (Element elemtr : elem1.children()) {
					if (elemtr.child(0).childNodeSize() > 0) {
						JobDetail job = JobDetail.builder().serialNo(elemtr.child(0).child(0).text())
								.job(elemtr.child(1).child(0).text()).jobNumber(elemtr.child(2).child(0).text())
								.status(elemtr.child(3).child(0).text()).jobType(elemtr.child(4).child(0).text())
								.actualStart(elemtr.child(5).child(0).text()).actualEnd(elemtr.child(6).child(0).text())
								.actualDuration(elemtr.child(7).child(0).text()).build();
						if (!jobNames.contains(job.getJob())) {
							jobs.add(job);
							jobNames.add(job.getJob());
						}
					}
				}
				jobs.forEach(System.out::println);
			}
		}
		return jobs;
	}

	public List<List<String>> prepareDataForExcel(String fileName) throws IOException {
		Document doc = Jsoup.parse(new File(fileName), ParserConstants.FILE_ENC_UTF8);
		Element tableTag = doc.getElementById(tableElementId);
		List<List<String>> jobs = new ArrayList<>();
		for (Element elem1 : tableTag.children()) {
			if (ParserConstants.TAG_TABLE_BODY.equals(elem1.tagName())) {
				for (Element elemtr : elem1.children()) {
					if (elemtr.child(0).childNodeSize() > 0) {
						List<String> tds = new ArrayList<>();
						elemtr.children().forEach(elem -> tds.add(elem.child(0).text()));
						jobs.add(tds);
					}
				}
				jobs.forEach(System.out::println);
			}
		}
		return jobs;
	}
}
