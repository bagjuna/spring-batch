package io.springbatch.springbatchlecture;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.integration.async.AsyncItemProcessor;
import org.springframework.batch.integration.async.AsyncItemWriter;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcCursorItemReader;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;
import org.springframework.core.task.TaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class MultiThreadStepConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final DataSource dataSource;

	@Bean
	public Job job() throws Exception {
		return jobBuilderFactory.get("batchJob")
			.incrementer(new RunIdIncrementer())
			.start(step1())
			.listener(new StopWatchJobListener())
			.build();
	}

	@Bean
	public Step step1() throws Exception{
		return stepBuilderFactory.get("step1")
			.<Customer, Customer>chunk(100)
			.reader(pagingItemReader())
			// .reader(customItemReader())
			.listener(new CustomItemReaderListener())
			.processor((ItemProcessor<? super Customer, ? extends Customer>)item -> item)
			.listener(new CustomItemProcessorListener())
			.writer(customItemWriter())
			.listener(new CustomItemWriterListener())
			.taskExecutor(taskExecutor())
			.build();
	}

	@Bean
	public TaskExecutor taskExecutor() {
		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
		taskExecutor.setCorePoolSize(4);
		taskExecutor.setMaxPoolSize(8);
		taskExecutor.setThreadNamePrefix("async-thread");

		return taskExecutor;
	}


	@Bean
	public JdbcCursorItemReader<Customer> customItemReader() throws Exception {
		return new JdbcCursorItemReaderBuilder()
			.name("jdbcCursorItemReader")
			.fetchSize(100)
			.sql("select id, first_name, last_name, birthdate from customer order by id")
			.beanRowMapper(Customer.class)
			.dataSource(dataSource)
			.build();

	}

	@Bean
	public JdbcPagingItemReader<Customer> pagingItemReader() {
		JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();

		reader.setDataSource(this.dataSource);
		reader.setPageSize(100);
		reader.setRowMapper(new CustomerRowMapper());

		MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
		queryProvider.setSelectClause("id, first_name, last_name, birthdate");
		queryProvider.setFromClause("from customer");

		Map<String, Order> sortKeys = new HashMap<>();

		sortKeys.put("id", Order.ASCENDING);

		queryProvider.setSortKeys(sortKeys);

		reader.setQueryProvider(queryProvider);

		return reader;


	}

	@Bean
	public JdbcBatchItemWriter customItemWriter() {
		JdbcBatchItemWriter<Customer> writer = new JdbcBatchItemWriter<>();
		writer.setDataSource(this.dataSource);
		writer.setSql("INSERT INTO customer2 (id, first_name, last_name, birthdate) VALUES (:id, :firstName, :lastName, :birthdate)");
		writer.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider<>());
		writer.afterPropertiesSet();

		return writer;
	}


}
