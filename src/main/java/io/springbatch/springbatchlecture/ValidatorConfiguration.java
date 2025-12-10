package io.springbatch.springbatchlecture;

import org.springframework.batch.core.BatchStatus;
import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.job.DefaultJobParametersValidator;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ValidatorConfiguration {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job batchJob1() {
		return this.jobBuilderFactory.get("batchJob")
			.start(step1())
			.next(step2())
			.next(step3())
			// .validator(new CustomJobParametersValidator())
			.validator(new DefaultJobParametersValidator(new String[] {"name", "date"}, new String[] {"count"}))
			.build();
	}

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
	public Step step3() {
		return this.stepBuilderFactory.get("step3")
			.tasklet((contribution, chunkContext) -> {
				chunkContext.getStepContext().getStepExecution().setStatus(BatchStatus.FAILED);
				contribution.setExitStatus(ExitStatus.STOPPED);
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
