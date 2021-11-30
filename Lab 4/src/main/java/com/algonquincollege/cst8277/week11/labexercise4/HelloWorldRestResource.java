/**
 * File: HelloWorldRestResource.java <br>
 * Course materials (21F) CST 8277
 * 
 * @author Teddy Yap
 * 
 * @author Shariar (Shawn) Emami
 * @date Mar 29, 2021
 * 
 * @author (original) Mike Norman
 * @date 2020 11
 */
package com.algonquincollege.cst8277.week11.labexercise4;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path( "/helloworld")
@Produces( MediaType.APPLICATION_JSON)
public class HelloWorldRestResource {

	@GET
	public Response sayHello() {
        // TODO - Update setStudentDetails() with your info
		MessageHolder mh = new MessageHolder( "Hello from Tomcat Embedded with JAX-RS")
				.setStudentDetails( "Chanreach Leang",	"040993529");
		return Response.ok( mh).build();
	}

}