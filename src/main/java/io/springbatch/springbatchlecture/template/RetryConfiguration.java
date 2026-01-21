package io.springbatch.springbatchlecture.template;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.backoff.FixedBackOffPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;

import io.springbatch.springbatchlecture.RetryableException;
import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RetryConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job job() {
		return jobBuilderFactory.get("batchJob2")
			.incrementer(new RunIdIncrementer())
			.start(step1())
			.build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
			.<String, Customer>chunk(5)
			.reader(reader())
			.processor(itemProcessor())
			.writer(items -> items.forEach(item -> System.out.println(item)))
			.faultTolerant()
			// .skip(RetryableException.class)
			// .skipLimit(2)
			// .retry(RetryableException.class)
			// .retryLimit(2)
			// .retryPolicy(retryPolicy())
			.build();
	}

	@Bean
	public ItemProcessor<? super String, Customer> processor() {
		return new RetryItemProcessor2();
	}

	@Bean
	public ItemReader<String> reader() {
		List<String> items = new ArrayList<>();
		for (int i = 0; i < 30; i++) {
			items.add(String.valueOf(i));
		}
		return new ListItemReader<>(items);
	}

	@Bean
	public ItemProcessor<? super String, Customer> itemProcessor() {
		return new RetryItemProcessor2();
	}

	@Bean
	public RetryPolicy retryPolicy() {
		Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap<>();
		exceptionClass.put(RetryableException.class, true);

		SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(2, exceptionClass);

		return simpleRetryPolicy;

	}

	@Bean
	public RetryTemplate retryTemplate() {
		Map<Class<? extends Throwable>, Boolean> exceptionClass = new HashMap<>();
		exceptionClass.put(RetryableException.class, true);

		FixedBackOffPolicy backOffPolicy = new FixedBackOffPolicy();
		backOffPolicy.setBackOffPeriod(2000); // 2 seconds

		SimpleRetryPolicy simpleRetryPolicy = new SimpleRetryPolicy(2, exceptionClass);
		RetryTemplate retryTemplate = new RetryTemplate();
		retryTemplate.setRetryPolicy(simpleRetryPolicy);

		// retryTemplate.setBackOffPolicy(backOffPolicy);

		return retryTemplate;
	}

}
