package io.springbatch.springbatchlecture;

import java.util.HashMap;
import java.util.Map;

import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.core.partition.support.Partitioner;
import org.springframework.batch.core.step.tasklet.Tasklet;
import org.springframework.batch.core.step.tasklet.TaskletStep;
import org.springframework.batch.item.ItemWriter;
import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
import org.springframework.batch.item.database.JdbcBatchItemWriter;
import org.springframework.batch.item.database.JdbcPagingItemReader;
import org.springframework.batch.item.database.Order;
import org.springframework.batch.item.database.support.MySqlPagingQueryProvider;
import org.springframework.beans.factory.annotation.Value;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.SimpleAsyncTaskExecutor;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class PartitioningConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final DataSource dataSource;

	@Bean
	public Job job(){
		return jobBuilderFactory.get("batchJob")
			.incrementer(new RunIdIncrementer())
			.start(masterStep())
			.build();
	}

	@Bean
	public Step masterStep(){
		return stepBuilderFactory.get("masterStep")
			.partitioner(slaveStep().getName(), partitioner())
			.step(slaveStep())
			.gridSize(4)
			.taskExecutor(new SimpleAsyncTaskExecutor())
			.build();
	}

	@Bean
	public Partitioner partitioner(){
		ColumnRangePartitioner columnRangePartitioner = new ColumnRangePartitioner();

		columnRangePartitioner.setColumn("id");
		columnRangePartitioner.setDataSource(this.dataSource);
		columnRangePartitioner.setTable("customer");

		return columnRangePartitioner;

	}

	@Bean
	public Step slaveStep(){
		return stepBuilderFactory.get("slaveStep")
			.<Customer, Customer>chunk(100)
			.reader(pagingItemReader(null, null))
			.writer(customerItemWriter())
			.build();
	}

	@Bean
	@StepScope
	public JdbcPagingItemReader<Customer> pagingItemReader(
		@Value("#{stepExecutionContext['minValue']}") Long minValue,
		@Value("#{stepExecutionContext['maxValue']}") Long maxValue
	) {
		System.out.println("reading : " + minValue + " to " + maxValue);
		JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();

		reader.setDataSource(this.dataSource);
		reader.setFetchSize(10);
		reader.setRowMapper(new CustomerRowMapper());

		MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
		queryProvider.setSelectClause("id, first_name, last_name, birthdate");
		queryProvider.setFromClause("from customer");
		queryProvider.setWhereClause("where id >= " + minValue + " and id < " + maxValue);

		Map<String, Order> sortKeys = new HashMap<>(1);

		sortKeys.put("id", Order.ASCENDING);

		queryProvider.setSortKeys(sortKeys);

		reader.setQueryProvider(queryProvider);

		return reader;
	}


	@Bean
	@StepScope
	public JdbcBatchItemWriter<Customer> customerItemWriter() {
		JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();

		itemWriter.setDataSource(this.dataSource);
		itemWriter.setSql("insert into customer2 values (:id, :firstName, :lastN8ame, :birthdate)");
		itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
		itemWriter.afterPropertiesSet();

		return itemWriter;
	}

	// @Bean
	// public TaskExecutor taskExecutor(){
	// 	ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
	// 	taskExecutor.setCorePoolSize(4);
	// 	taskExecutor.setMaxPoolSize(8);
	// 	taskExecutor.setThreadNamePrefix("async-thread-");
	// 	return taskExecutor;
	// }

}
