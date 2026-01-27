package io.springbatch.springbatchlecture;

import java.util.List;

import org.springframework.batch.core.ItemWriteListener;

public class CustomItemWriterListener implements ItemWriteListener<Customer> {

	@Override
	public void beforeWrite(List<? extends Customer> items) {

	}

	@Override
	public void afterWrite(List<? extends Customer> items) {
		System.out.println("Thread : " + Thread.currentThread().getName() + " write items size : " + items.size());
	}

	@Override
	public void onWriteError(Exception e, List<? extends Customer> items) {

	}

}
