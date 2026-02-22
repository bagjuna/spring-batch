package io.springbatch.springbatchlecture;

import java.util.List;

import javax.batch.api.chunk.listener.ItemWriteListener;

public class CustomItemWriteListener implements ItemWriteListener {
	@Override
	public void beforeWrite(List<Object> list) throws Exception {
		System.out.println(">> before Write");
	}

	@Override
	public void afterWrite(List<Object> list) throws Exception {
		System.out.println(">> after Write");
	}

	@Override
	public void onWriteError(List<Object> list, Exception e) throws Exception {
		System.out.println(">> on Write error");
	}
}
