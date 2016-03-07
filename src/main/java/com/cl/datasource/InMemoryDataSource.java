package com.cl.datasource;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.apache.cxf.common.util.StringUtils;
import org.apache.log4j.Logger;
import org.springframework.context.annotation.Scope;
import org.springframework.stereotype.Component;

import com.cl.data.Interval;
import com.cl.data.Load;
import com.cl.util.LoadUtil;
import com.cl.util.TimeUtil;

@Component
@Scope("singleton")
public class InMemoryDataSource implements DataSource<Load> {
	private static final Logger logger = Logger.getLogger(InMemoryDataSource.class);
	// <servername, load>
	Map<String, List<Load>> loadsPerMinute;
	// <hour, <servername, index>>
	Map<String, Map<String, Integer>> hourHostIndex;

	public InMemoryDataSource() {
		loadsPerMinute = new HashMap<String, List<Load>>();
		hourHostIndex = new HashMap<String, Map<String, Integer>>();
	}

	public void put(Load load) {
		if (load == null || StringUtils.isEmpty(load.getUtctime()) || StringUtils.isEmpty(load.getServername())) {
			throw new IllegalArgumentException("load/load time/laod server can't be null or empty");
		}
		List<Load> loads = loadsPerMinute.get(load.getServername());
		if (loads == null) {
			loads = new ArrayList<Load>();
			loads.add(load);
			loadsPerMinute.put(load.getServername(), loads);
		} else {
			loads.add(load);
		}
		String time = load.getUtctime();
		String hour = time.substring(0, 14);
		Map<String, Integer> hostIndex = hourHostIndex.get(hour);
		if (hostIndex == null) {
			hostIndex = new HashMap<String, Integer>();
			hostIndex.put(load.getServername(), loads.size() - 1);
			hourHostIndex.put(hour, hostIndex);
		}
	}

	public List<Load> get(String server, Date date, int duration, Interval interval) {
		if (!TimeUtil.isValidMonitorDate(date)) {
			throw new IllegalArgumentException("date is valid " + date);
		}
		if (duration <= 0 || interval == null) {
			logger.error("invalid duration :" + duration + " interval: " + interval);
			throw new IllegalArgumentException("duration must > 0, duration: " + duration + ",interval can't be null.");
		}
		List<Load> loads = loadsPerMinute.get(server);
		if (loads == null) {
			logger.info(server + "is not monitored.");
			return new ArrayList<Load>();
		}
		if (interval.equals(Interval.MINUTE)) {
			return getByMinute(server, date, duration, interval);
		} else {
			return getByHour(server, date, duration, interval);
		}
	}

	private List<Load> getByHour(String server, Date date, int duration, Interval interval) {
		List<Load> result = new ArrayList<Load>();
		Calendar cal = Calendar.getInstance();
		cal.setTime(date);
		for(int hour = 0;  hour < duration; hour++){
			List<Load> loadsForOneHour = getByMinute(server, cal.getTime(), 60, Interval.MINUTE);
			Load hourLoad = LoadUtil.average(loadsForOneHour);
			if (hourLoad != null){
				result.add(hourLoad);
			}
			cal.add(Calendar.HOUR, 1);
		}
		return result;
	}
	
	
	private List<Load> getByMinute(String server, Date date, int duration, Interval interval) {
		List<Load> loads = loadsPerMinute.get(server);
		String startMinute = TimeUtil.getStartMinute(date);
		String endMinute = TimeUtil.getEndMinute(date, duration);
		int hourindex = getHourlyIndex(server, date);
		int start = findStartingIndex(startMinute, loads, hourindex);
		int index = start;

		List<Load> result = new ArrayList<Load>();
		for (int count = 0; count < duration && index + count < loads.size(); count++) {
			Load load = loads.get(index + count);
			if (load.getUtctime().compareTo(endMinute) <= 0) {
				result.add(load);
			} else {
				break;
			}
		}
		return result;
	}

	private int findStartingIndex(String startMinute, List<Load> loads, int hourindex) {
		int i = hourindex;
		for (; i < loads.size(); i++) {
			int compare = loads.get(i).getUtctime().compareTo(startMinute);
			if (compare == 0)
				return i;
			if (compare > 0)
				break;
		}
		return i--;
	}

	private int getHourlyIndex(String server, Date date) {
		String hour = TimeUtil.toHourString(date);
		Map<String, Integer> hostIndex = hourHostIndex.get(hour);
		if (hostIndex == null) {
			return 0;
		}
		Integer index = hostIndex.get(server);
		if (index == null) {
			return 0;
		}
		return index;
	}

	// for testing only
	public void clear() {
		hourHostIndex.clear();
		loadsPerMinute.clear();
	}

}
