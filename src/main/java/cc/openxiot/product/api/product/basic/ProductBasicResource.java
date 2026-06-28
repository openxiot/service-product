package cc.openxiot.product.api.product.basic;

import cc.openxiot.account.db.history.History;
import cc.openxiot.common.resource.AbstractResource;
import cc.openxiot.common.exception.OxException;
import cc.openxiot.common.response.OxResponse;
import cc.openxiot.common.role.OxRole;
import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.by.Updater;
import cn.geekcity.xiot.spec.codec.vertx.product.basic.ProductBasicCodec;
import cn.geekcity.xiot.spec.definition.urn.Urn;
import cn.geekcity.xiot.spec.definition.urn.UrnType;
import cn.geekcity.xiot.spec.lifecycle.Lifecycle;
import cn.geekcity.xiot.spec.product.basic.ProductBasic;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/v1/product/basic")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Product Basic", description = "Product Basic API")
@RequestScoped
public class ProductBasicResource extends AbstractResource {

    @Inject
    Logger logger;

    @Inject
    ProductBasicService service;

    @POST
    @Path("/one")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    @Operation(summary = "add", description = "add product")
    @APIResponse(responseCode = "200", description = "success")
    @APIResponse(responseCode = "401", description = "unauthorized")
    @APIResponse(responseCode = "403", description = "forbidden")
    public Response add(JsonObject item) {
        logger.infov("add: {0}", item);

        try {
            ProductBasic basic = ProductBasicCodec.decode(item);
            Creator creator = getCreator(basic.organization());
            basic.creator(creator);
            basic.updater(new Updater().id(creator.id()).name(creator.name()).timestamp(creator.timestamp()));

            service.add(basic);

            JsonObject object = ProductBasicCodec.encode(basic);

            History.addDeveloper(basic.organization(), creator.id(), "ADD", "ProductBasic", object.toString());

            return OxResponse.ok(object);
        } catch (OxException | IllegalArgumentException e) {
            return OxResponse.error(e);
        }
    }

    @DELETE
    @Path("/one")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    public Response delete(
            @QueryParam("organizationId") String organizationId,
            @QueryParam("productId") String productId
    ) {
        logger.infov("delete, {0}/{1}", organizationId, productId);

        try {
            checkManagerPermission(organizationId);

            ProductBasic basic = service.findById(productId);
            if (basic == null) {
                return OxResponse.error("product not found");
            }

            service.delete(productId);

            History.addDeveloper(organizationId, getName(), "DELETE", "ProductBasic", ProductBasicCodec.encode(basic).toString());

            return OxResponse.ok();
        } catch (OxException | IllegalArgumentException e) {
            return OxResponse.error(e);
        }
    }

    @PUT
    @Path("/one")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    public Response update(JsonObject item) {
        logger.infov("update, {0}", item);

        try {
            ProductBasic basic = ProductBasicCodec.decode(item);

            checkManagerPermission(basic.organization());

            Updater updater = getUpdater(basic.organization());
            basic.updater(updater);

            service.update(basic);

            History.addDeveloper(basic.organization(), getName(), "UPDATE", "Item", item.toString());

            return OxResponse.ok();
        } catch (OxException | IllegalArgumentException e) {
            return OxResponse.error(e);
        }
    }

    @PUT
    @Path("/one/lifecycle/{productId}/{lifecycle}")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    public Response updateLifecycle(
            @PathParam("productId") String productId,
            @PathParam("lifecycle") String lifecycle
    ) {
        logger.infov("updateLifecycle, {0} => {1}", productId, lifecycle);

        try {
            ProductBasic basic = service.findById(productId);
            if (basic == null) {
                return OxResponse.error("product not found");
            }

            checkManagerPermission(basic.organization());
            Updater updater = getUpdater(basic.organization());

            service.update(productId, Lifecycle.of(lifecycle), updater);

            return OxResponse.ok();
        } catch (OxException | IllegalArgumentException e) {
            return OxResponse.error(e);
        }
    }

    @GET
    @Path("/one")
    public Response getOne(
            @QueryParam("productId") String productId
    ) {
        logger.infov("getOne, {0}", productId);

        ProductBasic basic = service.findById(productId);
        if (basic == null) {
            return OxResponse.error("product not found");
        } else {
            JsonObject object = ProductBasicCodec.encode(basic);
            return OxResponse.ok(object);
        }
    }

    @GET
    @Path("/public")
    public Response getPublic() {
        logger.infov("getPublic");

        List<ProductBasic> products = service.findAll();
        JsonArray array = ProductBasicCodec.encode(products);
        return OxResponse.ok(array);
    }

    @GET
    @Path("/visible/{organization}")
    public Response getVisible(
            @PathParam("organization") String organization
    ) {
        logger.infov("getVisible: {0}", organization);

        if (organization.isBlank()) {
            return OxResponse.error("organization is empty");
        }

        List<ProductBasic> products= service.findByOrganization(organization);
        JsonArray array = ProductBasicCodec.encode(products);
        return OxResponse.ok(array);
    }
}
