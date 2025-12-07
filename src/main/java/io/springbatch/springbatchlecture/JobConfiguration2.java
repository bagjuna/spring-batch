package io.springbatch.springbatchlecture;

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
public class JobConfiguration2 {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job job() {
		return this.jobBuilderFactory.get("batchJob2")
			.start(step3())
			.next(step4())
			.build();
	}

	@Bean
	public Step step3() {
		return this.stepBuilderFactory.get("step3")
			.tasklet((contribution, chunkContext) -> {
				System.out.println("step3 has executed");
				return RepeatStatus.FINISHED;
			})
			.build();
	}

	@Bean
	public Step step4() {
		return this.stepBuilderFactory.get("step4")
			.tasklet((contribution, chunkContext) -> {
				System.out.println("step4 has executed");
				return RepeatStatus.FINISHED;
			})
			.build();
	}


}
