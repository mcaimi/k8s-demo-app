package org.redhat;

import javax.inject.Inject;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import org.jboss.resteasy.annotations.jaxrs.PathParam;

@Path("/services")
public class TimeWasterResource {
    @Inject
    private TimeWasterService timeWaster;
    
    @GET
    @Path("/delay/{milliseconds}")
    @Produces(MediaType.TEXT_PLAIN)
    public String wasteTime(@PathParam long milliseconds) throws InterruptedException {
        try {
            return timeWaster.wasteTime(milliseconds);
        }
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new InterruptedException(e.getMessage());
        }
    }
}