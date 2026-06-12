package cc.openxiot.product.api.specification.property;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

@Path("/v1/spec/property")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "property", description = "Property API")
@RequestScoped
public class PropertyResource {

    @Inject
    Logger logger;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from PropertyResource";
    }

}
