package io.springbatch.springbatchlecture;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.builder.FlowBuilder;
import org.springframework.batch.core.job.flow.Flow;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JobConfiguration {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job batchJob2() {
		return this.jobBuilderFactory.get("batchJob2")
			.start(flow())
			.next(step5())
			.end()
			.build();
	}
/*
	@Bean
	public Step step1() {
		return this.stepBuilderFactory.get("step1")
			.tasklet((contribution, chunkContext) -> {
				System.out.println("step1 has executed");
				return RepeatStatus.FINISHED;
			})
			.build();
	}

	@Bean
	public Step step2() {
		return this.stepBuilderFactory.get("step2")
			.tasklet((contribution, chunkContext) -> {
				System.out.println("step2 has executed");
				return RepeatStatus.FINISHED;
			})
			.build();
	}

	@Bean
	public Job batchJob1() {
		return this.jobBuilderFactory.get("batchJob1")
			.start(step1())
			.next(step2())
			.build();
	}
	*/

	@Bean
	public Flow flow() {
		FlowBuilder<Flow> flowBuilder = new FlowBuilder<>("flow");
		flowBuilder.start(step3())
			.next(step4())
			.end();

		return flowBuilder.build();
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

	@Bean
	public Step step5() {
		return this.stepBuilderFactory.get("step5")
			.tasklet((contribution, chunkContext) -> {
				System.out.println("step5 has executed");
				return RepeatStatus.FINISHED;
			})
			.build();

	}
}
