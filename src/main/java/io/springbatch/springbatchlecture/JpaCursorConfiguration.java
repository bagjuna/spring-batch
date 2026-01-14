package io.springbatch.springbatchlecture;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.JpaCursorItemReader;
import org.springframework.batch.item.database.builder.JpaCursorItemReaderBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Configuration
public class JpaCursorConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final int chunkSize = 5;
	private final EntityManagerFactory em;

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
			.<Customer, Customer>chunk(chunkSize)
			.reader(customItemReader())
			.writer(customItemWriter())
			.build();
	}

	@Bean
	public JpaCursorItemReader<Customer> customItemReader() {

		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("firstname", "A%");

		return new JpaCursorItemReaderBuilder()
			.name("jpaCursorItemReader")
			.queryString("select c from Customer c where first_name like :firstname")
			.entityManagerFactory(em)
			.parameterValues(parameters)
			// .maxItemCount(10)
			// .currentItemCount(2)
			.build();

	}

	@Bean
	public ItemWriter<Customer> customItemWriter() {
		return items -> {
			for (Customer customer : items) {
				System.out.println(customer);
			}
		};
	}

}
