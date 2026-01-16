package io.springbatch.springbatchlecture;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.batch.item.adapter.ItemWriterAdapter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcBatchItemWriterBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class ItemWriterAdapterConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final DataSource dataSource;
	private final EntityManagerFactory entityManagerFactory;
	@Value("${file.path}")
	private String filePath;

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
			.reader(
				new ItemReader<String>() {
					int i = 0;

					@Override
					public String read() throws
						Exception,
						UnexpectedInputException,
						ParseException,
						NonTransientResourceException {
						i++;
						return i > 10 ? null : "item" + i;
					}
				}
			)
			.writer(customItemWriter())
			.build();
	}

	@Bean
	public ItemWriter<? super String> customItemWriter() {

		ItemWriterAdapter<String> writer = new ItemWriterAdapter<>();
		writer.setTargetObject(customService());
		writer.setTargetMethod("customWrite");

		return writer;
	}

	@Bean
	public CustomService customService() {
		return new CustomService();
	}

}
