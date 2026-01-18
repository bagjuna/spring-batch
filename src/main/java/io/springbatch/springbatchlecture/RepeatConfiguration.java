package io.springbatch.springbatchlecture;

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
import org.springframework.batch.repeat.CompletionPolicy;
import org.springframework.batch.repeat.RepeatCallback;
import org.springframework.batch.repeat.RepeatContext;
import org.springframework.batch.repeat.RepeatStatus;
import org.springframework.batch.repeat.exception.ExceptionHandler;
import org.springframework.batch.repeat.exception.SimpleLimitExceptionHandler;
import org.springframework.batch.repeat.policy.CompositeCompletionPolicy;
import org.springframework.batch.repeat.policy.SimpleCompletionPolicy;
import org.springframework.batch.repeat.policy.TimeoutTerminationPolicy;
import org.springframework.batch.repeat.support.RepeatTemplate;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import lombok.RequiredArgsConstructor;

@Configuration
@RequiredArgsConstructor
public class RepeatConfiguration {

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
				public String read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
					i++;
					return i > 10 ? null : "item" + i;
				}
			})
			.processor(new ItemProcessor<String, String>() {
				RepeatTemplate repeatTemplate = new RepeatTemplate();

				@Override
				public String process(String item) throws Exception {

				/*	repeatTemplate.setCompletionPolicy(new SimpleCompletionPolicy(3));
					repeatTemplate.setCompletionPolicy(new TimeoutTerminationPolicy(3000));

					CompositeCompletionPolicy completionPolicy = new CompositeCompletionPolicy();
					CompletionPolicy[] completionPolicies = new CompletionPolicy[] {new SimpleCompletionPolicy(3), new TimeoutTerminationPolicy(3000)};

					completionPolicy.setPolicies(completionPolicies);
					repeatTemplate.setCompletionPolicy(completionPolicy);
					*/

					repeatTemplate.setExceptionHandler(simpleLimitExceptionHandler());

					repeatTemplate.iterate(new RepeatCallback() {
						@Override
						public RepeatStatus doInIteration(RepeatContext repeatContext) throws Exception {
							System.out.println("repeatTemplate item is testing");
							throw new RuntimeException("Exception is occurred");
							// return RepeatStatus.CONTINUABLE;
						}
					});

					return item;
				}
			})
			.writer(items -> System.out.println(items))
			.build();
	}

	@Bean
	public ExceptionHandler simpleLimitExceptionHandler() {
		return new SimpleLimitExceptionHandler(3);
	}
}
