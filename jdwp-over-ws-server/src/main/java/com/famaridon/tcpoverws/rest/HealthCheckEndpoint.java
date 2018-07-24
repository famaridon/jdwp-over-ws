package com.famaridon.tcpoverws.rest;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Response;

@Path("/ping")
public class HealthCheckEndpoint {

  @GET
  @Produces("text/plain")
  public Response doGet() {
    return Response.ok("pong").build();
  }
}