package org.redhat;

import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.inject.Inject;

import org.jboss.resteasy.annotations.jaxrs.PathParam;

import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.Tags;

@Path("/hello")
public class HelloResource {

    @Inject
    MeterRegistry registry;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        registry.counter("hello.resource.hello.count").increment();
        return "hello";
    }

    @GET
    @Path("/{name}")
    @Produces(MediaType.TEXT_PLAIN)
    public String helloName(@PathParam String name) {
        registry.counter("hello.resource.helloname.count", Tags.of("name", name)).increment();
        return "Hello " + name + "!";
    }
}