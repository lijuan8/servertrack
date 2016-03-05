package com.cl.service;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;

@Path("/display")
public class DisplayingService {

	@GET
	@Path("/{servername}")
	@Produces("text/plain")
	public String getLoad(@PathParam("servername") String servername) {
		System.out.println("abc" + servername);
		return servername;
		//return Response.status(200).entity(servername).build();
	}
}
