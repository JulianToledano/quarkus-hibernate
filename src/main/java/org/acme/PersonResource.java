package org.acme;

import javax.transaction.Transactional;
import javax.ws.rs.Consumes;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.PathParam;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;

import org.checkerframework.checker.units.qual.s;
import org.jboss.logging.Logger;

@Path("/dbc")
public class PersonResource {
    
    private static final Logger LOGGER = Logger.getLogger(PersonResource.class);

    @POST
    @Transactional
    @Consumes(MediaType.APPLICATION_JSON)
    @Path("/create/person")
    public Response createPerson(Person person) {
        //Person s = new Person("John", "Connor", 26);
        if (person != null){
            person.persist();
            return Response.ok().build();
        }
        return Response.noContent().build();
    }

    @GET   
    @Transactional
    @Path("/retrieve/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPerson(@PathParam("name") String name){
        Person person = Person.findByName(name);
        if (person != null)
            return Response.ok(person).build();            
        return Response.noContent().build();
    }

}