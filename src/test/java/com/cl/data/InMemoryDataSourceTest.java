package com.cl.data;

import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.cl.Factory.LoadFactory;
import com.cl.datasource.InMemoryDataSource;
import com.cl.util.TimeUtil;

public class InMemoryDataSourceTest {

	private InMemoryDataSource tested = new InMemoryDataSource();
	private Calendar cal = Calendar.getInstance();

	@Before
	public void setUp() {
		Load load = new Load("server1");
		load.setUtctime("2016-01-01-02-03");
		tested.put(load);
	}

	@Test
	public void getNonMonitoredServer() {
		cal.set(2016, 01, 07, 10, 10);
		Assert.assertTrue(tested.get("non-existing-server", cal.getTime(), 10, Interval.MINUTE).size() == 0	);
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidDate() {
		Date date = new Date();
		date.setTime(System.currentTimeMillis() + 1 * 60 * 60 * 1000);
		Assert.assertTrue(null == tested.get("server1", date, 10, Interval.MINUTE));
	}

	@Test(expected = IllegalArgumentException.class)
	public void testInvalidInterval() {
		tested.get("server1", new Date(), 1, Interval.MINUTE);
	}

	@Test
	public void testGet() {
		cal.set(2016, 01, 07, 10, 10);
		String server = "server1";
		for (int i = 0; i < 10; i++) {
			Load load = new Load(server);
			load.setUtctime(TimeUtil.toMinuteString(cal.getTime()));
			load.setCpu((short) (1000 + i));
			load.setMemory((short) (2000 + i));
			tested.put(load);
			cal.add(Calendar.MINUTE, 1);
		}

		cal.set(2016, 01, 07, 10, 10);
		List<Load> result = tested.get("server1", cal.getTime(), 10, Interval.MINUTE);
		Assert.assertTrue(result.size() == 10);
		Assert.assertTrue(result.get(9).getMemory() == 2009);

		cal.set(2016, 01, 07, 10, 14);
		result = tested.get("server1", cal.getTime(), 4, Interval.MINUTE);
		Assert.assertTrue(result.size() == 4);
		Assert.assertTrue(result.get(3).getMemory() == 2007);
	}

	@Test
	public void testGetDataWithMissingTracking() {
		loadTestData();
		// start from 10:11, only for 10 minutes
		cal.set(2016, 01, 07, 10, 11);
		List<Load> result = tested.get("server1", cal.getTime(), 10, Interval.MINUTE);
		Assert.assertTrue(result.size() == 6); // only 5
		Assert.assertTrue(result.get(4).getMemory() == 2009);

		// start from missing minute, for 4 minutes
		cal.set(2016, 01, 07, 10, 14);
		result = tested.get("server1", cal.getTime(), 4, Interval.MINUTE);
		Assert.assertTrue(result.size() == 2);
		Assert.assertTrue(result.get(1).getMemory() == 2007);

		// start from missing minute for 20 minutes, ( no data after 10:29)
		cal.set(2016, 01, 07, 10, 15);
		result = tested.get("server1", cal.getTime(), 20, Interval.MINUTE);
		Assert.assertTrue(result.size() == 8);
		Assert.assertTrue(result.get(7).getMemory() == 2019);

	}

	@Test
	public void testFromHourWithoutRecord() {
		loadTestData();
		// start from minutes no load data
		cal.set(2016, 01, 07, 9, 55);
		List<Load> result = tested.get("server1", cal.getTime(), 30, Interval.MINUTE);
		Assert.assertTrue(result.size() == 8);
		Assert.assertTrue(result.get(7).getMemory() == 2015);
	}

	@Test
	public void testForMinutesNoRecordedData() {
		loadTestData();
		// start from minutes no load data
		cal.set(2016, 01, 07, 9, 55);
		List<Load> result = tested.get("server1", cal.getTime(), 10, Interval.MINUTE);
		Assert.assertTrue(result.size() == 0);
	}

	@Test
	public void testGetLoadByHour() {
		loadTestData();
		cal.set(2016, 01, 07, 10, 00);
		// last one hour from 10:00
		List<Load> result = tested.get("server1", cal.getTime(), 1, Interval.HOUR);
		Assert.assertTrue(result.size() == 1);
		Assert.assertTrue(result.get(0).getServername().equals("server1"));

		// last one hour from 10:10
		cal.set(2016, 01, 07, 10, 10);
		List<Load> result1 = tested.get("server1", cal.getTime(), 1, Interval.HOUR);
		Assert.assertTrue(result1.size() == 1);
		// from 10:00 to 10:10 no data, so they equal
		Assert.assertTrue(result.get(0).getCpu() == result1.get(0).getCpu());

		// last one hour from 9:10 to 10:10, load is recorded from 10:11, so
		// result is empty
		cal.set(2016, 01, 07, 9, 10);
		List<Load> result0910 = tested.get("server1", cal.getTime(), 1, Interval.HOUR);
		Assert.assertTrue(result0910.size() == 0);

		// last one hour from 9:11 to 10:11, load is recorded from 10:11
		cal.set(2016, 01, 07, 9, 11);
		List<Load> result0911 = tested.get("server1", cal.getTime(), 1, Interval.HOUR);
		Assert.assertTrue(result0911.size() == 1);
		Assert.assertTrue(result0911.get(0).getCpu() == 1001);
		
		// last three hour from 9:11; from 9:11 to 10:11;, from 10:11 to 11:11, from 11:11 to 12:11
		cal.set(2016, 01, 07, 9, 11);
		List<Load> result0911for3 = tested.get("server1", cal.getTime(), 3, Interval.HOUR);
		Assert.assertTrue(result0911for3.size() == 2);
		Assert.assertTrue(result0911.get(0).getCpu() == 1001);
	}
	
	@Test
	public void testMultipleServers() {
		cal.set(2016, 01, 07, 10, 10); //01/07/2016 10:10
		tested.put(createLoad("server1", TimeUtil.toMinuteString(cal.getTime()), (short)1));
		tested.put(createLoad("server2", TimeUtil.toMinuteString(cal.getTime()), (short)1));
		cal.add(Calendar.MINUTE, 2);
		tested.put(createLoad("server1", TimeUtil.toMinuteString(cal.getTime()), (short)1));
		tested.put(createLoad("server2", TimeUtil.toMinuteString(cal.getTime()), (short)1));
		cal.set(2016, 01, 07, 10, 10);
		List<Load> result = tested.get("server1", cal.getTime(), 5, Interval.MINUTE);
		Assert.assertTrue(result.size() == 2);
	}
	
	
	@Test
	public void testGetLoadByHours() {
		loadMultipleHourData();
		cal.set(2016, 01, 07, 9, 10);
		List<Load> result = tested.get("server1", cal.getTime(), 5, Interval.HOUR);
		Assert.assertTrue(result.size() == 4);
		Assert.assertTrue(result.get(3).getCpu() == 5);
	}
	
	void loadMultipleHourData(){
		cal.set(2016, 01, 07, 10, 10); //01/07/2016 10:10
		tested.put(createLoad("server1", TimeUtil.toMinuteString(cal.getTime()), (short)1));
		
		cal.add(Calendar.HOUR, 1); //11:10
		tested.put(createLoad("server1", TimeUtil.toMinuteString(cal.getTime()), (short)2));
		
		cal.add(Calendar.MINUTE, 5); //11:15
		tested.put(createLoad("server1", TimeUtil.toMinuteString(cal.getTime()), (short)3));
		
		cal.add(Calendar.HOUR, 1); //12:15
		tested.put(createLoad("server1", TimeUtil.toMinuteString(cal.getTime()), (short)4));
		
		cal.add(Calendar.MINUTE, 15); //12:30
		tested.put(createLoad("server1", TimeUtil.toMinuteString(cal.getTime()), (short)5));
		
		cal.add(Calendar.MINUTE, 15); //12:45
		tested.put(createLoad("server1", TimeUtil.toMinuteString(cal.getTime()), (short)6));
	}
	
	private Load createLoad(String server, String time, short cpu){
		return LoadFactory.create(server, time, cpu, (short) 9);
	}

	// recorded load from 10:10 for 20 minutes but missing all even minutes,
	// only 10:11, 10:13, 10:15, 10:17, ... 10:29 recorded
	void loadTestData() {
		cal.set(2016, 01, 07, 10, 10);
		String server = "server1";
		for (int i = 0; i < 20; i++) {
			if (i % 2 == 0) {
				cal.add(Calendar.MINUTE, 1);
				continue;
			}
			Load load = LoadFactory.create(server, cal.getTime(), (short) (1000 + i), (short) (2000 + i));
			tested.put(load);
			cal.add(Calendar.MINUTE, 1);
		}
	}

	@After
	public void clear() {
		tested.clear();
	}

}
