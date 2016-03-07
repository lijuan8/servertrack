package com.cl.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;

public class TimeUtil {
	private static SimpleDateFormat toMinuteFormatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm");
	private static SimpleDateFormat toHourFormatter = new SimpleDateFormat("yyyy-MM-dd-HH");

	public static Date toDate(String date) throws ParseException {
		return toMinuteFormatter.parse(date);
	}
	public static boolean isValidMonitorDate(Date date) {
		if (date == null)
			return false;
		return date.before(new Date());
	}

	public static String toHourString(Date date) {
		if (date == null)
			throw new IllegalArgumentException("date is null");
		return toHourFormatter.format(date);
	}
	
	public static String getEndHour(Date date, int duration) {
		if (date == null)
			throw new IllegalArgumentException("date is null");
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.HOUR, duration);
		return toHourFormatter.format(date);
	}
	
	public static String toMinuteString(Date date) {
		if (date == null)
			throw new IllegalArgumentException("date is null");
		return toMinuteFormatter.format(date);
	}

	public static String getStartMinute(Date date) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		return toMinuteFormatter.format(cal.getTime());
	}

	public static String getEndMinute(Date date, int duration) {
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		cal.add(Calendar.MINUTE, duration);
		return toMinuteFormatter.format(cal.getTime());
	}
}
