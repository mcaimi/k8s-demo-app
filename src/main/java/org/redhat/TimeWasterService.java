package org.redhat;

import javax.enterprise.context.RequestScoped;
import javax.ws.rs.GET;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
@RequestScoped
public class TimeWasterService {
    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String wasteTime(long milliseconds) throws InterruptedException {
        String response = "";
        try {
            Thread.sleep(milliseconds);
            response = "Wasted a little time: " + milliseconds + "ms.";
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        return response;
    }
}