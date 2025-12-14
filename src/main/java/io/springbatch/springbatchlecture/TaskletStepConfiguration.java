package io.springbatch.springbatchlecture;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class TaskletStepConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job batchJob() {
		return this.jobBuilderFactory.get("batchJob")
			.start(taskletStep())
			.build();
	}

	@Bean
	public Step taskletStep() {
		return stepBuilderFactory.get("taskStep")
			.tasklet((contribution, chunkContext) -> {
				System.out.println("step has executed");
				return RepeatStatus.FINISHED;
			})
			.allowStartIfComplete(true)
			.build();
	}

	@Bean
	public Step chunkStep() {
		return stepBuilderFactory.get("chunkStep")
			.<String, String>chunk(3)
			.reader(() -> new ListItemReader<>(List.of("item1", "item2", "item3", "item4", "item5")).read())
			.processor(new ItemProcessor<String, String>() {
						   @Override
						   public String process(String item) throws Exception {
							   return item.toUpperCase();
						   }
					   }
			)
			.writer(items -> {
				items.forEach(item -> System.out.println("item = " + item));
			})
			.build();
	}

}
