package com.model;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class JobDetail {
	
	String serialNo;
	
	String job;
	
	String jobNumber;
	
	String status;
	
	String jobType;
	
	String actualStart;
	
	String actualEnd;
	
	String actualDuration;
	
	JobDuration jobDuration;
	
	public JobDuration creationJobDuration() {
		JobDuration duration = new JobDuration();
		duration.job = this.job;
		duration.actualStart = this.actualStart;
		duration.actualEnd = this.actualEnd;
		duration.actualDuration = this.actualDuration;
		this.jobDuration = duration;
		return this.jobDuration;
	}
	
	public JobDetail(String job, String actualStart, String actualEnd, String actualDuration ) {
		JobDuration duration = new JobDuration();
		duration.job = this.job;
		duration.actualStart = this.actualStart;
		duration.actualEnd = this.actualEnd;
		duration.actualDuration = this.actualDuration;
		this.jobDuration = duration;
	}

	public JobDetail(String serialNo, String job, String jobNumber, String status, String jobType,
			String actualStart, String actualEnd, String actualDuration, JobDuration jobDuration) {
		super();
		this.serialNo = serialNo;
		this.job = job;
		this.jobNumber = jobNumber;
		this.status = status;
		this.jobType = jobType;
		this.actualStart = actualStart;
		this.actualEnd = actualEnd;
		this.actualDuration = actualDuration;
		JobDuration duration = new JobDuration();
		duration.job = this.job;
		duration.actualStart = this.actualStart;
		duration.actualEnd = this.actualEnd;
		duration.actualDuration = this.actualDuration;
		this.jobDuration = duration;
		this.jobDuration = jobDuration;
	}
}
