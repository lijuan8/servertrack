package com.cl.service;

import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;

import org.springframework.beans.factory.annotation.Autowired;

import com.cl.Factory.LoadFactory;
import com.cl.data.Load;
import com.cl.datasource.DataSource;

@Path("/track")
public class TrackingService {
	@Autowired
	DataSource<Load> dataSource;

	//track for servername's cpu and memory at now
	@POST
	@Path("/{servername}/{cpu}/{memory}")
	public String trackLoad(@PathParam("servername") String servername, @PathParam("cpu") short cpu,
			@PathParam("memory") short memory) {
		Load load = LoadFactory.create(servername, cpu, memory);
		dataSource.put(load);
		return "success";
	}

	@POST
	@Path("/{servername}/{datetime}/{cpu}/{memory}")
	//track servername's cpu and memory at {datetime}
	//datetime: yyyy-MM-dd-HH-mm
	public String test(@PathParam("servername") String servername, @PathParam("datetime") String datetime,
			@PathParam("cpu") short cpu, @PathParam("memory") short memory) {
	
		Load load = LoadFactory.create(servername, datetime, cpu, memory);
		dataSource.put(load);
		return "success";
	}

	public void setDataSource(DataSource<Load> dataSource) {
		this.dataSource = dataSource;
	}

}
