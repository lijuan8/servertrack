package com.cl.datasource;

import java.util.Date;
import java.util.List;

import com.cl.data.Interval;

public interface DataSource<T> {

	public void put(T t);
	public List<T> get(String hostname, Date from, int duration, Interval interval); //duration and inverval using seconds
}
