package org.acme;

import javax.transaction.Transactional;
import javax.ws.rs.GET;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

@Path("/dbc")
public class ExampleResource {

    @GET
    @Transactional
    @Path("/create")
    public Response createPerson() {
        Person person = new Person("John", "Connor", 26);
        person.persist();
        return Response.ok().build();
    }
}