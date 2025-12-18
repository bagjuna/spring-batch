package io.springbatch.springbatchlecture;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class BatchStatusExitStatusConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
/*
	@Bean
	public Job batchJob() {
		return this.jobBuilderFactory.get("batchJob")
			.start(step1())
			.next(step2())
			.build();
	}
	*/

	@Bean
	public Job batchJob() {
		return this.jobBuilderFactory.get("batchJob")
			.start(step1())
			.on("FAILED")  // step1의 ExitStatus가 FAILED일 때
			.to(step2())   // step2로 이동
			.end()         // flow 종료
			.build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
			.tasklet((contribution, chunkContext) -> {
				System.out.println("step1 has executed");
				contribution.setExitStatus(ExitStatus.FAILED);
				return RepeatStatus.FINISHED;
			})
			.build();
	}

	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2")
			.tasklet((contribution, chunkContext) -> {
				System.out.println("step2 has executed");
				// contribution.setExitStatus(ExitStatus.FAILED);
				return RepeatStatus.FINISHED;
			})
			.build();
	}


}
