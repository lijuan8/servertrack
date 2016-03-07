package com.cl.util;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;

import org.junit.Assert;
import org.junit.Test;

public class TimeUtilTest {

	private Calendar cal = Calendar.getInstance();
	
	@Test
	public void testParse() throws ParseException {
		Date date = TimeUtil.toDate("2016-01-01-10-10");
		Assert.assertTrue(date.getMinutes() == 10);
	}

	@Test
	public void testInvalidDate() {
		Date date = new Date();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, 100);
		Assert.assertFalse("Monitor date can't be later than now()", TimeUtil.isValidMonitorDate(cal.getTime()));
	}

	@Test
	public void testValidDate() {
		Date date = new Date();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, -100);
		Assert.assertTrue("Monitor last 100 minutes", TimeUtil.isValidMonitorDate(cal.getTime()));
	}

	@Test
	public void testToHourString() {
		cal.set(2016, 02, 07, 10, 10);
		System.out.println(TimeUtil.toHourString(cal.getTime()));
		Assert.assertTrue(TimeUtil.toHourString(cal.getTime()).equalsIgnoreCase("2016-03-07-10"));
	}

	@Test
	public void testGetStartMinute() {
		cal.set(2016, 02, 07, 10, 10);
		System.out.println(TimeUtil.getStartMinute(cal.getTime()));
		Assert.assertTrue(TimeUtil.getStartMinute(cal.getTime()).equalsIgnoreCase("2016-03-07-10-10"));
	}

	@Test
	public void testGetEndMinute() {
		cal.set(2016, 02, 07, 10, 10);
		System.out.println(TimeUtil.getEndMinute(cal.getTime(), 10));
		Assert.assertTrue(TimeUtil.getEndMinute(cal.getTime(), 10).equalsIgnoreCase("2016-03-07-10-20"));
	}
}
