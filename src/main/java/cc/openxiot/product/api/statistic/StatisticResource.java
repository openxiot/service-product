package cc.openxiot.product.api.statistic;

import cc.openxiot.account.db.developer.organization.DeveloperOrganization;
import cc.openxiot.product.db.product.ProductRepository;
import cc.openxiot.product.db.specification.SpecificationRepository;
import cc.openxiot.product.db.template.TemplateRepository;
import cc.openxiot.product.prepared.SpecificationPrepared;
import cc.openxiot.common.response.OxResponse;
import cc.openxiot.product.prepared.TemplatePrepared;
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

import java.io.IOException;

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
    SpecificationPrepared specificationPrepared;

    @Inject
    TemplatePrepared templatePrepared;

    @GET
    @Path("/")
    public Response get() {
        logger.infov("get");

        try {
            long products = product.count();
            long specifications = specification.count() + specificationPrepared.count();
            long templates = template.count() + templatePrepared.count();
            long organizations = DeveloperOrganization.count();

            JsonObject o = new JsonObject();
            o.put("products", products);
            o.put("prepared/specifications", specifications);
            o.put("prepared/templates", templates);
            o.put("organizations", organizations);

            return OxResponse.ok(o);
        } catch (IOException e) {
            return OxResponse.error(e);
        }
    }
}
