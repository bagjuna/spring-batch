package io.springbatch.springbatchlecture;

import java.util.List;

import org.springframework.batch.item.ItemWriter;

public class CustomItemWriter implements ItemWriter<String> {

	int count = 0;

	@Override
	public void write(List<? extends String> items) throws Exception {
		for (String item : items) {
			if (count % 2 == 0) {
				count++;
			} else if (count % 2 == 1) {
				count++;
				throw new CustomRetryException("failed");
			}

			System.out.println("write : " + item);
		}
	}
}
