package io.springbatch.springbatchlecture;

import java.util.Arrays;
import java.util.List;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.file.builder.FlatFileItemWriterBuilder;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.FileSystemResource;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class FlatFilesDelimitedConfiguration {
	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;

	@Bean
	public Job job() throws Exception {
		return jobBuilderFactory.get("batchJob")
			.incrementer(new RunIdIncrementer())
			.start(step1())
			.build();
	}


	public Step step1() throws Exception {
		return stepBuilderFactory.get("step1")
			.<Customer, Customer>chunk(10)
			.reader(customItemReader())
			.writer(customItemWriter())
			.build();
	}

	@Bean
	public ItemWriter<? super Customer> customItemWriter() throws Exception {
		return new FlatFileItemWriterBuilder<Customer>()
			.name("flatFileItemWriter")
			.resource(new FileSystemResource("/absolute/path/src/main/resources/customers.txt"))
			.append(true)
			.shouldDeleteIfEmpty(true)
			.delimited()
			.delimiter("|")
			.names(new String[] { "id", "name", "age" })
			.build();
	}

	@Bean
	public ItemReader<? extends Customer> customItemReader() {
		List<Customer> customers = Arrays.asList(new Customer(1L, "hong gil dong1", 41),
			new Customer(2L, "hong gil dong2", 42),
			new Customer(3L, "hong gil dong3", 43)
		);
		ListItemReader<Customer> reader = new ListItemReader<>(customers);
		return reader;
	}

}
