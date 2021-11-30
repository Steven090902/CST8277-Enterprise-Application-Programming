/*****************************************************************
 * File: HealthCheckResource.java
 * Course materials (21F) CST 8277
 * @author Teddy Yap
 * @date  2020 11
 * @author (original) Mike Norman
 */
package com.algonquincollege.cst8277.week11.labexercise4;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/healthCheck")
@Produces(MediaType.APPLICATION_JSON)
public class HealthCheckResource {

    @GET
    public Response sayHello() {
        // TODO - DiskSpaceHealthCheck, metrics: response time, CPU load, Memory ...
        return Response.ok("{\"Response Time\":\"10ms\",\"CPU Load\":\"10%\",\"Memory Usage\":\"1.1GB\",\"msg\":\"Hello from healthcheck!\"}").build();
    }

}