package io.springbatch.springbatchlecture;


import java.util.Arrays;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.support.ListItemReader;
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

	@Bean
	public Job job(){
		return jobBuilderFactory.get("batchJob")
			.incrementer(new RunIdIncrementer())
			.start(step1())
			.build();
	}

	@Bean
	public Step step1(){
		return stepBuilderFactory.get("step1")
			.<Integer, String>chunk(10)
			.listener(new CustomChunkListener())
			.listener(new CustomItemReadListener())
			.listener(new CustomItemProcessListener())
			.listener(new CustomItemWriteListener())
			.reader(listItemReader())
			.processor((ItemProcessor)item -> "item = " + item)
			.writer(
				(items) -> System.out.println("items = " + items))
			.build();
	}

	@Bean
	public ItemReader<Integer> listItemReader() {
		List<Integer> list = Arrays.asList(1, 2, 3, 4, 5, 6, 7, 8, 9, 10);
		return new ListItemReader<>(list);
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
