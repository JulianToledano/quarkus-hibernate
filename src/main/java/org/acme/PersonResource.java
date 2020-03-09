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
import org.eclipse.microprofile.metrics.MetricUnits;
import org.eclipse.microprofile.metrics.annotation.Counted;
import org.eclipse.microprofile.metrics.annotation.Timed;
import org.jboss.logging.Logger;

@Path("/dbc")
public class PersonResource {
    
    private static final Logger LOGGER = Logger.getLogger(PersonResource.class);

    @POST
    @Transactional
    @Counted(name = "timesPersist", description = "How many times a person had been added.")
    @Timed(name = "persistTimer", description = "A measure of how long it to persist a person.", unit = MetricUnits.MILLISECONDS)
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
    @Counted(name = "timesRetrieved", description = "How many times a person had been retrieved.")
    @Timed(name = "retrieveTimer", description = "A measure of how long it takes to retrieve a person.", unit = MetricUnits.MILLISECONDS)
    @Path("/retrieve/{name}")
    @Produces(MediaType.APPLICATION_JSON)
    public Response getPerson(@PathParam("name") String name){
        Person person = Person.findByName(name);
        if (person != null)
            return Response.ok(person).build();            
        return Response.noContent().build();
    }

}