package pe.archety;

import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/service")
public class ArchetypeService {
    @GET
    @Path("/helloworld")
    public String helloWorld() {
        return "Hello World!";
    }
}
