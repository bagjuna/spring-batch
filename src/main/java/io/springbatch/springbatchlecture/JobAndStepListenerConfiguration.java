package io.springbatch.springbatchlecture;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class JobAndStepListenerConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final CustomStepExecutionListener customStepExecutionListener;

	@Bean
	public Job job(){
		return jobBuilderFactory.get("batchJob")
			.incrementer(new RunIdIncrementer())
			.start(step1())
			.next(step2())
			.listener(new CustomJobExecutionListener())
			// .incrementer(new CustomAnnotationJobExecutionListener())
			.build();
	}

	@Bean
	public Step step1(){
		return stepBuilderFactory.get("step1")
			.tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
			.listener(new CustomStepExecutionListener())
			.build();
	}

	@Bean
	public Step step2(){
		return stepBuilderFactory.get("step2")
			.tasklet((contribution, chunkContext) -> RepeatStatus.FINISHED)
			.listener(customStepExecutionListener)
			.build();
	}

	// @Bean
	// public TaskExecutor taskExecutor(){
	// 	ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
	// 	taskExecutor.setCorePoolSize(4);
	// 	taskExecutor.setMaxPoolSize(8);
	// 	taskExecutor.setThreadNamePrefix("not-safety-thread-");
	// 	return taskExecutor;
	// }

}
