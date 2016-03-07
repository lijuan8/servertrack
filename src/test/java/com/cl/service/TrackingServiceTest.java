package com.cl.service;

import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.cl.data.Interval;
import com.cl.datasource.DataSource;

public class TrackingServiceTest {

	private TrackingService tested = new TrackingService();
	@Test
	public void test() {
		tested.setDataSource(new DataSource(){
			public void put(Object t) {
				
			}
			public List get(String hostname, Date from, int duration, Interval interval) {
				return null;
			}
			
		});
		Assert.assertTrue("success".equalsIgnoreCase(tested.trackLoad("server1", (short)100, (short)100)));
	}

}
