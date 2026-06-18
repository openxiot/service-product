package cc.openxiot.product.api.statistic;

import cc.openxiot.product.db.organization.Organization;
import cc.openxiot.product.db.product.ProductRepository;
import cc.openxiot.product.db.specification.SpecificationRepository;
import cc.openxiot.product.db.template.TemplateRepository;
import cc.openxiot.product.prepared.SpecificationPrepared;
import cc.openxiot.product.response.OxResponse;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

@Path("/v1/statistic")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Statistic", description = "Statistic API")
@RequestScoped
public class StatisticResource {

    @Inject
    Logger logger;

    @Inject
    ProductRepository product;

    @Inject
    SpecificationRepository specification;

    @Inject
    TemplateRepository template;

    @Inject
    SpecificationPrepared prepared;

    @GET
    @Path("/")
    public Response get() {
        logger.infov("get");

        long products = product.count();
        long specifications = specification.count() + prepared.count();
        long templates = template.count();
        long organizations = Organization.count();

        JsonObject o = new JsonObject();
        o.put("products", products);
        o.put("specifications", specifications);
        o.put("templates", templates);
        o.put("organizations", organizations);

        return OxResponse.ok(o);
    }
}
