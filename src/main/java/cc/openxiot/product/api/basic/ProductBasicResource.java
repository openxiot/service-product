package cc.openxiot.product.api.basic;

import cc.openxiot.product.db.product.ProductEntity;
import cc.openxiot.product.response.OxResponse;
import cn.geekcity.xiot.spec.codec.vertx.product.basic.ProductBasicCodec;
import cn.geekcity.xiot.spec.product.basic.ProductBasic;
import io.vertx.core.json.JsonArray;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.GET;
import jakarta.ws.rs.Path;
import jakarta.ws.rs.Produces;
import jakarta.ws.rs.QueryParam;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.enums.SchemaType;
import org.eclipse.microprofile.openapi.annotations.media.Content;
import org.eclipse.microprofile.openapi.annotations.media.Schema;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/v1/basic")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "product basic", description = "Product Basic API")
@RequestScoped
public class ProductBasicResource {

    @Inject
    Logger logger;

    @Inject
    ProductBasicService service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus REST";
    }

    @GET
    @Path("/all")
//    @RolesAllowed({OxRole.DEVELOPER})
    @Operation(
            summary = "get products basic info",
            description = "get all products(basic)"
    )
    @APIResponse(
            responseCode = "200",
            description = "success",
            content = @Content(schema = @Schema(
                    type = SchemaType.ARRAY,
                    implementation = ProductEntity.class
            ))
    )
    @APIResponse(responseCode = "401", description = "unauthorized")
    @APIResponse(responseCode = "403", description = "forbidden")
    public Response getAll(
            @QueryParam("organizationId") String organizationId
    ) {
        logger.infov("getBasicAll, organizationId: {0}", organizationId);

        List<ProductBasic> products;
        if (organizationId == null || organizationId.isBlank()) {
            products = service.findAll();
        } else {
            products = service.findByOrganization(organizationId);
        }

        JsonArray array = ProductBasicCodec.encode(products);

        return OxResponse.ok(array);
    }
}
