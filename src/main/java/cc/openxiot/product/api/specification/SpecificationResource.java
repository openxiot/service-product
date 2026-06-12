package cc.openxiot.product.api.specification;

import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

@Path("/v1/spec/namespace")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "namespace", description = "Namespace API")
@RequestScoped
public class SpecificationResource {

    @Inject
    Logger logger;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from SpecificationResource";
    }

}
