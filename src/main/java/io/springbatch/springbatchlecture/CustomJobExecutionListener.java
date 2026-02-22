package io.springbatch.springbatchlecture;

import java.util.Date;

import org.springframework.batch.core.JobExecution;
import org.springframework.batch.core.JobExecutionListener;
import org.springframework.stereotype.Component;

@Component
public class CustomJobExecutionListener implements JobExecutionListener {

	@Override
	public void beforeJob(JobExecution jobExecution) {
		System.out.println("Job is started");
		System.out.println("JobName : " + jobExecution.getJobInstance().getJobName());
	}

	@Override
	public void afterJob(JobExecution jobExecution) {
		Date startTime = jobExecution.getStartTime();
		Date endTime = jobExecution.getEndTime();
		long time = endTime.getTime() - startTime.getTime();

		System.out.println("총 소요시간 : " + time);

	}
}
