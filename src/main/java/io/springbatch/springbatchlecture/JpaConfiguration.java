package io.springbatch.springbatchlecture;

import java.util.HashMap;
import java.util.Map;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.sql.DataSource;

import org.springframework.batch.core.Job;
import org.springframework.batch.core.Step;
import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
import org.springframework.batch.core.launch.support.RunIdIncrementer;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.batch.item.ItemWriter;
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
public class JpaConfiguration {

	private final JobBuilderFactory jobBuilderFactory;
	private final StepBuilderFactory stepBuilderFactory;
	private final DataSource dataSource;
	private final EntityManagerFactory entityManagerFactory;
	@Value("${file.path}")
	private String filePath;

	@Bean
	public Job job()  {
		return jobBuilderFactory.get("batchJob")
			.incrementer(new RunIdIncrementer())
			.start(step1())
			.build();
	}

	@Bean
	public Step step1() {
		return stepBuilderFactory.get("step1")
			.<Customer, Customer2>chunk(10)
			.reader(customItemReader())
			.processor(customItemProcessor())
			.writer(customItemWriter())
			.build();
	}

	@Bean
	public ItemProcessor<? super Customer, ? extends Customer2> customItemProcessor() {
		return new CustomItemProcessor();
	}

	@Bean
	public ItemWriter<? super Customer2> customItemWriter() {
		return new JdbcBatchItemWriterBuilder<Customer2>()
			.dataSource(dataSource)
			.sql("insert into customer2 (id, first_name, last_name, birthdate) values (:id, :firstName, :lastName, :birthdate)")
			.beanMapped()
			.build();
	}

	@Bean
	public JdbcPagingItemReader<Customer> customItemReader() {

		JdbcPagingItemReader<Customer> reader = new JdbcPagingItemReader<>();

		reader.setDataSource(this.dataSource);
		reader.setFetchSize(10);
		reader.setRowMapper(new CustomerRowMapper());

		MySqlPagingQueryProvider queryProvider = new MySqlPagingQueryProvider();
		queryProvider.setSelectClause("id, first_name, last_name, birthdate");
		queryProvider.setFromClause("from customer");
		queryProvider.setWhereClause("where first_name like :firstname");

		Map<String, Order> sortKeys = new HashMap<>(1);

		sortKeys.put("id", Order.ASCENDING);
		queryProvider.setSortKeys(sortKeys);
		reader.setQueryProvider(queryProvider);

		HashMap<String, Object> parameters = new HashMap<>();
		parameters.put("firstname", "A%");

		reader.setParameterValues(parameters);

		return reader;
	}

}
