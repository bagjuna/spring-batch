package io.springbatch.springbatchlecture;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.StepContribution;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.scope.context.ChunkContext;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class JobParameterConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job job() {
		return jobBuilderFactory.get("job")
			.start(step1())
			.next(step2())
			.build();
	}

	private Step step1() {
		return stepBuilderFactory.get("step1")
			.tasklet((contribution, chunkContext) -> {
				JobParameters jobParameters = contribution.getStepExecution().getJobExecution().getJobParameters();
				Map<String, String> params1 = new HashMap<>();

				System.out.println("jobParameters.getString(\"name\") = " + jobParameters.getString("name"));
				System.out.println("jobParameters.getLong(\"seq\") = " + jobParameters.getLong("seq"));
				System.out.println("jobParameters.getDate(\"date\") = " + jobParameters.getDate("date"));
				System.out.println("jobParameters.getDouble(\"age\") = " + jobParameters.getDouble("age"));

				Map<String, Object> jobParameters1 = chunkContext.getStepContext().getJobParameters();


				System.out.println("step1 was executed");
				return RepeatStatus.FINISHED;
			})
			.build();

	}


	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2")
			.tasklet(new Tasklet() {
				@Override
				public RepeatStatus execute(StepContribution contribution, ChunkContext chunkContext) throws Exception {
					System.out.println("step2 has executed");
					return RepeatStatus.FINISHED;
				}
			})
			.build();

	}



}
