package org.redhat;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import org.jboss.resteasy.annotations.jaxrs.PathParam;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

@Path("/services")
public class TimeWasterResource {
    @Inject
    TimeWasterService timeWaster;

    @Inject
    MeterRegistry registry;    
    
    @GET
    @Path("/delay/{milliseconds}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response wasteTime(@PathParam long milliseconds) throws InterruptedException {
        registry.counter("time_resource_wastetime_count", Tags.of("milliseconds", String.valueOf(milliseconds))).increment();
        try {
            return Response.status(200).entity(timeWaster.wasteTime(milliseconds)).build();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedException(e.getMessage());
        }
    }

    @GET
    @Path("/delay/{milliseconds}/{code}")
    @Produces(MediaType.TEXT_PLAIN)
    public Response wasteTimeWithCode(@PathParam long milliseconds, @PathParam int code) throws InterruptedException {
        registry.counter("time_resource_wastetime_code_millis_count", Tags.of("milliseconds", String.valueOf(milliseconds))).increment();
        registry.counter("time_resource_wastetime_code_code_count", Tags.of("code", String.valueOf(code))).increment();
        
        try {
            return Response.status(code).entity(timeWaster.wasteTime(milliseconds)).build();
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedException(e.getMessage());
        }
    }
}