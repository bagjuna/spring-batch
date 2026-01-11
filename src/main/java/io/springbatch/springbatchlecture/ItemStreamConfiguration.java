package io.springbatch.springbatchlecture;

import java.util.ArrayList;
import java.util.List;


import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.item.ItemStreamWriter;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class ItemStreamConfiguration {


	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;


	@Bean
	public Job job() {
		return jobBuilderFactory.get("batchJob")
			.start(step1())
			.next(step2())
			.build();
	}


	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
			.chunk(5)
			.reader(itemReader())
			.writer(itemWriter())
			.build();
	}

	@Bean
	public ItemStreamWriter itemWriter() {
		return new CustomItemWriter();
	}

	public CustomItemStreamReader itemReader() {
		List<String> items = new ArrayList<>(10);

		for (int i = 0; i <= 10; i++) {
			items.add(String.valueOf(i));
		}

		return  new CustomItemStreamReader(items);
	}

	@Bean
	public Step step2() {
		return stepBuilderFactory.get("step2")
			.tasklet((contribution, chunkContext) -> {
				System.out.println("step2 has executed");
				return RepeatStatus.FINISHED;
			})
			.build();
	}
}
