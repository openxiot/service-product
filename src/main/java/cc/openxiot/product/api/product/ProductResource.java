package cc.openxiot.product.api.product;

import cc.openxiot.product.api.product.basic.ProductBasicService;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

@Path("/v1/product")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "product", description = "Product API")
@RequestScoped
public class ProductResource {

    @Inject
    Logger logger;

    @Inject
    ProductBasicService service;

    @GET
    @Produces(MediaType.TEXT_PLAIN)
    public String hello() {
        return "Hello from Quarkus REST";
    }

//    @GET
//    @Path("/full/all")
////    @RolesAllowed({OxRole.DEVELOPER})
//    @Operation(
//            summary = "get products",
//            description = "get all products(basic,wizard,instances,panels,firmwares,manual)"
//    )
//    @APIResponse(
//            responseCode = "200",
//            description = "success",
//            content = @Content(schema = @Schema(
//                    type = SchemaType.ARRAY,
//                    implementation = ProductEntity.class
//            ))
//    )
//    @APIResponse(responseCode = "401", description = "unauthorized")
//    @APIResponse(responseCode = "403", description = "forbidden")
//    public Response getFullAll(
//            @QueryParam("organizationId") String organizationId
//    ) {
//        logger.infov("getFullAll, organizationId: {0}", organizationId);
//
//        List<Product> products;
//        if (organizationId == null || organizationId.isBlank()) {
//            products = service.findAll();
//        } else {
//            products = service.findByOrganization(organizationId);
//        }
//
//        JsonArray array = ProductCodec.encode(products);
//
//        return OxResponse.ok(array);
//    }
}
