import javax.ws.rs.GET;
import javax.ws.rs.Path;

@Path("/template")
public class TemplateResource {
    @GET
    public String templateHelloWorld() {
        return "Hello World";
    }
}
