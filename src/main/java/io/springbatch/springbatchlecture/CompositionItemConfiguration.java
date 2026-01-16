package io.springbatch.springbatchlecture;

import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.support.CompositeItemProcessor;
import org.springframework.batch.item.support.builder.CompositeItemProcessorBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class CompositionItemConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job job() {
		return jobBuilderFactory.get("batchJob")
			.incrementer(new RunIdIncrementer())
			.start(step1())
			.build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
			.<String, String>chunk(10)
			.reader(new ItemReader<>() {
				int i = 0;

				@Override
				public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
					i++;
					return i > 10 ? null : "item" + i;
				}
			})
			.processor(customItemProcessor())
			.writer(items -> System.out.println(items))
			.build();
	}

	@Bean
	public ItemProcessor<String, String> customItemProcessor() {
		List itemProcessors = List.of(new CustomItemProcessor(), new CustomItemProcessor2());
		return new CompositeItemProcessorBuilder<>()
			.delegates(itemProcessors)
			.build();
	}

}
