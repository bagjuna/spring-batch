package io.springbatch.springbatchlecture;

import java.util.ArrayList;
import java.util.List;

import org.springframework.aop.support.AopUtils;
import org.springframework.batch.item.ItemReader;

import io.micrometer.core.lang.Nullable;

public class LinkedListItemReader<T> implements ItemReader<T> {

	private List<T> list;


	public LinkedListItemReader(List<T> list) {
		if (AopUtils.isAopProxy(list)) {
			this.list = list;
		}
		else {
			this.list = new ArrayList<>(list);
		}
	}

	@Nullable
	@Override
	public T read()  {

		if(!list.isEmpty()) {
			T remove = list.remove(0);
			return remove;
		}
		return null;
	}
}
