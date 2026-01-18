package io.springbatch.springbatchlecture;

import java.util.HashMap;
import java.util.Map;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.step.skip.LimitCheckingItemSkipPolicy;
import org.springframework.batch.core.step.skip.SkipPolicy;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class SkipConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job job() {
		return jobBuilderFactory.get("batchJob")
			.incrementer(new RunIdIncrementer())
			.start(step1())
			.build();
	}

	public Step step1() {
		return stepBuilderFactory.get("step1")
			.<String, String>chunk(5)
			.reader(new ItemReader<String>() {
				int i = 0;
				@Override
				public String read() throws SkippableException {
					i++;
					if (i == 3) {
						throw new SkippableException("skip");
					}
					System.out.println("ItemReader : " + i);
					return i > 20 ? null : String.valueOf(i);
				}
			})
			.processor(itemProcessor())
			.writer(itemWriter())
			.faultTolerant()
			.skipPolicy(limitCheckingItemSkipPolicy())
			// .skip(SkippableException.class)
			// .skipLimit(4)
			.build();
	}

	@Bean
	public SkipPolicy limitCheckingItemSkipPolicy() {

		Map<Class<? extends Throwable>, Boolean> exceptionsToSkip = new HashMap<>();
		exceptionsToSkip.put(SkippableException.class, true);

		LimitCheckingItemSkipPolicy limitCheckingItemSkipPolicy = new LimitCheckingItemSkipPolicy(3, exceptionsToSkip);

		return limitCheckingItemSkipPolicy;

	}

	@Bean
	public SkipItemWriter itemWriter() {
		return new SkipItemWriter();
	}

	@Bean
	public ItemProcessor<String, String> itemProcessor() {
		return new SkipItemProcessor();
	}
}
