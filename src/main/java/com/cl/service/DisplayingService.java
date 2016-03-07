package com.cl.service;

import java.text.ParseException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

import org.springframework.beans.factory.annotation.Autowired;

import com.cl.data.Interval;
import com.cl.data.Load;
import com.cl.datasource.DataSource;
import com.cl.util.TimeUtil;

@Path("/display")
public class DisplayingService {

	@Autowired
	private DataSource<Load> dataSource;

	//for the past duration of minutes or hour from now
	@GET
	@Path("/{servername}/{duration}/{interval}")
	@Produces("application/json")
	public List<Load> getLoad(@PathParam("servername") String servername, @PathParam("duration") int duration,
			@PathParam("interval") String interval) {
		Date now = new Date();
		Calendar cal = Calendar.getInstance();
		cal.setTime(now);
		if (interval.equalsIgnoreCase("m")) {
			cal.add(Calendar.MINUTE, -duration);
			return dataSource.get(servername, cal.getTime(), duration, Interval.MINUTE);
		} else if (interval.equalsIgnoreCase("h")) {
			cal.add(Calendar.HOUR, -duration);
			return dataSource.get(servername, cal.getTime(), duration, Interval.HOUR);
		}
		throw new IllegalArgumentException("interval has to be m or h");
	}

	@GET
	@Path("/{servername}/{datetime}/{duration}/{interval}")
	@Produces("application/json")
	//from {datetime} for duration minutes or hours 
	//datetime yyyy-MM-dd-HH-mm
	public List<Load> getLoadFrom(@PathParam("servername") String servername, @PathParam("datetime") String datetime,
			@PathParam("duration") int duration, @PathParam("interval") String interval) throws ParseException {
		if (interval.equalsIgnoreCase("m")) {
			return dataSource.get(servername, TimeUtil.toDate(datetime), duration, Interval.MINUTE);
		} else if (interval.equalsIgnoreCase("h")) {
			return dataSource.get(servername, TimeUtil.toDate(datetime), duration, Interval.HOUR);
		}
		throw new IllegalArgumentException("interval has to be m or h");
	}

	public void setDataSource(DataSource<Load> dataSource) {
		this.dataSource = dataSource;
	}

}
