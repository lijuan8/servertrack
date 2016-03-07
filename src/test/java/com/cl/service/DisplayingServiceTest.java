package com.cl.service;

import static org.mockito.Mockito.when;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mockito;

import com.cl.data.Interval;
import com.cl.data.Load;
import com.cl.datasource.DataSource;

public class DisplayingServiceTest {
	DisplayingService tested = new DisplayingService();
	List<Load> result = new ArrayList<Load>();

	@Before
	public void setUp() {
		DataSource<Load> ds = Mockito.mock(DataSource.class);
		when(ds.get(Mockito.anyString(), Mockito.any(Date.class), Mockito.anyInt(), (Interval) Mockito.any()))
				.thenReturn(result);
		tested.setDataSource(ds);
	}

	@Test
	public void test() {
		List<Load> loads = tested.getLoad("server1", 20, "m");
		Assert.assertTrue(result.equals(loads));
	}
	
	@Test(expected=IllegalArgumentException.class)
	public void testException() {
		List<Load> loads = tested.getLoad("server1", 20, "d");
	}

}
