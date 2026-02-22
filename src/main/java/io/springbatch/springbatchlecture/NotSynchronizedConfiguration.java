package io.springbatch.springbatchlecture;

//
// import javax.sql.DataSource;
//
// import org.springframework.batch.core.ItemReadListener;
// import org.springframework.batch.core.Job;
// import org.springframework.batch.core.Step;
// import org.springframework.batch.core.configuration.annotation.JobBuilderFactory;
// import org.springframework.batch.core.configuration.annotation.StepBuilderFactory;
// import org.springframework.batch.core.configuration.annotation.StepScope;
// import org.springframework.batch.core.launch.support.RunIdIncrementer;
// import org.springframework.batch.item.database.BeanPropertyItemSqlParameterSourceProvider;
// import org.springframework.batch.item.database.JdbcBatchItemWriter;
// import org.springframework.batch.item.database.JdbcCursorItemReader;
// import org.springframework.batch.item.database.builder.JdbcCursorItemReaderBuilder;
// import org.springframework.context.annotation.Bean;
// import org.springframework.context.annotation.Configuration;
// import org.springframework.core.task.TaskExecutor;
// import org.springframework.jdbc.core.BeanPropertyRowMapper;
// import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
//
// import lombok.RequiredArgsConstructor;
//
// @Configuration
// @RequiredArgsConstructor
// public class NotSynchronizedConfiguration {
//
// 	private final JobBuilderFactory jobBuilderFactory;
// 	private final StepBuilderFactory stepBuilderFactory;
// 	private final DataSource dataSource;
//
// 	@Bean
// 	public Job job(){
// 		return jobBuilderFactory.get("batchJob")
// 			.incrementer(new RunIdIncrementer())
// 			.start(step1())
// 			.build();
// 	}
//
// 	@Bean
// 	public Step step1(){
// 		return stepBuilderFactory.get("step1")
// 			.<Customer, Customer>chunk(10)
// 			.listener(new ItemReadListener<Customer>() {
// 				@Override
// 				public void beforeRead() {
//
// 				}
//
// 				@Override
// 				public void afterRead(Customer customer) {
// 					System.out.println("Thread : " + Thread.currentThread().getName() + ", item.getId() = " + customer.getId());
// 				}
//
// 				@Override
// 				public void onReadError(Exception e) {
//
// 				}
// 			})
// 			.reader(customItemReader())
// 			.writer(customerItemWriter())
// 			.taskExecutor(taskExecutor())
// 			.build();
// 	}
//
// 	@Bean
// 	@StepScope
// 	public JdbcCursorItemReader<Customer> customItemReader() {
// 		return new JdbcCursorItemReaderBuilder<Customer>()
// 			.dataSource(this.dataSource)
// 			.rowMapper(new BeanPropertyRowMapper<>(Customer.class))
// 			.sql("select id, first_name, last_name, birthdate from customer")
// 			.name("NotSafetyReader")
// 			.build();
// 	}
//
//
// 	@Bean
// 	@StepScope
// 	public JdbcBatchItemWriter<Customer> customerItemWriter() {
// 		JdbcBatchItemWriter<Customer> itemWriter = new JdbcBatchItemWriter<>();
//
// 		itemWriter.setDataSource(this.dataSource);
// 		itemWriter.setSql("insert into customer2 values (:id, :firstName, :lastName, :birthdate)");
// 		itemWriter.setItemSqlParameterSourceProvider(new BeanPropertyItemSqlParameterSourceProvider());
// 		itemWriter.afterPropertiesSet();
//
// 		return itemWriter;
// 	}
//
// 	@Bean
// 	public TaskExecutor taskExecutor(){
// 		ThreadPoolTaskExecutor taskExecutor = new ThreadPoolTaskExecutor();
// 		taskExecutor.setCorePoolSize(4);
// 		taskExecutor.setMaxPoolSize(8);
// 		taskExecutor.setThreadNamePrefix("not-safety-thread-");
// 		return taskExecutor;
// 	}
//
// }
