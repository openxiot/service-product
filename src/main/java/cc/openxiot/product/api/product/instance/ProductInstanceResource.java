package cc.openxiot.product.api.product.instance;

import cc.openxiot.product.db.history.History;
import cc.openxiot.product.exception.OxException;
import cc.openxiot.product.resource.AbstractResource;
import cc.openxiot.product.response.OxResponse;
import cc.openxiot.product.role.OxRole;
import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.by.Updater;
import cn.geekcity.xiot.spec.codec.vertx.instance.DeviceInstanceCodec;
import cn.geekcity.xiot.spec.codec.vertx.product.instance.ProductInstanceCodec;
import cn.geekcity.xiot.spec.definition.urn.Urn;
import cn.geekcity.xiot.spec.definition.urn.UrnType;
import cn.geekcity.xiot.spec.instance.DeviceInstance;
import cn.geekcity.xiot.spec.lifecycle.Lifecycle;
import cn.geekcity.xiot.spec.product.instance.ProductInstance;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.util.Arrays;
import java.util.List;

@Path("/v1/product/instance")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Product Instance", description = "Product Instance API")
@RequestScoped
public class ProductInstanceResource extends AbstractResource {

    @Inject
    Logger logger;

    @Inject
    ProductInstanceService service;

    @POST
    @Path("/one")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    @Operation(summary = "add", description = "add product")
    @APIResponse(responseCode = "200", description = "success")
    @APIResponse(responseCode = "401", description = "unauthorized")
    @APIResponse(responseCode = "403", description = "forbidden")
    public Response add(JsonObject object) {
        logger.infov("add: {0}", object);

        try {
            DeviceInstance instance = DeviceInstanceCodec.decode(object);
            Creator creator = getCreator(instance.type().organization());

            service.add(instance, creator);

            History.addDeveloper(instance.type().organization(), creator.id(), "ADD", "ProductInstance", object.toString());

            return OxResponse.ok();
        } catch (OxException | IllegalArgumentException e) {
            return OxResponse.error(e.getMessage());
        }
    }

    @DELETE
    @Path("/one")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    public Response delete(
            @QueryParam("type") String type
    ) {
        logger.infov("delete, {0}", type);

        try {
            Urn urn = new Urn(UrnType.DEVICE, type, true);

            checkManagerPermission(urn.organization());

            service.deleteByType(urn);

            History.addDeveloper(urn.organization(), getName(), "DELETE", "ProductInstance", type);

            return OxResponse.ok();
        } catch (OxException | IllegalArgumentException e) {
            return OxResponse.error(e.getMessage());
        }
    }

    @PUT
    @Path("/one")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    public Response update(JsonObject object) {
        logger.infov("update, {0}", object);

        try {
            DeviceInstance instance = DeviceInstanceCodec.decode(object);
            Updater updater = getUpdater(instance.type().organization());

            service.update(instance, updater);

            History.addDeveloper(instance.type().organization(), updater.id(), "UPDATE", "ProductInstance", object.toString());

            return OxResponse.ok();
        } catch (OxException | IllegalArgumentException e) {
            return OxResponse.error(e.getMessage());
        }
    }

    @PUT
    @Path("/one/lifecycle/{type}/{lifecycle}")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    public Response updateLifecycle(
            @PathParam("type") String type,
            @PathParam("lifecycle") String lifecycle
    ) {
        logger.infov("updateLifecycle, {0} => ", type, lifecycle);

        try {
            Urn urn = new Urn(UrnType.DEVICE, type, true);

            checkManagerPermission(urn.organization());

            Updater updater = getUpdater(urn.organization());
            service.update(urn, Lifecycle.fromString(lifecycle), updater);

            JsonObject o = new JsonObject().put("type", type).put("lifecycle", lifecycle);
            History.addDeveloper(urn.organization(), updater.id(), "UPDATE", "ProductInstance.Lifecycle", o.encode());

            return OxResponse.ok();
        } catch (OxException | IllegalArgumentException e) {
            return OxResponse.error(e.getMessage());
        }
    }

    @GET
    @Path("/one/{type}")
    @Operation(
            summary = "get product instance",
            description = "get product instance"
    )
    @APIResponse(responseCode = "200", description = "success")
    public Response getOne(
            @PathParam("type") String type
    ) {
        logger.infov("getOne: {0}", type);

        try {
            Urn urn = new Urn(Arrays.asList(UrnType.DEVICE, UrnType.GROUP), type, true);
            DeviceInstance instance = service.findInstanceByType(urn);
            JsonObject object = DeviceInstanceCodec.encode(instance);
            return OxResponse.ok(object);
        } catch (IllegalArgumentException e) {
            return OxResponse.error(e);
        }
    }

    @GET
    @Path("/many")
    @Operation(
            summary = "get product instances",
            description = "get product instances"
    )
    @APIResponse(responseCode = "200", description = "success")
    public Response getManyByProductId(
            @QueryParam("productId") String productId
    ) {
        logger.infov("getManyByProductId: {0}", productId);

        try {
            List<ProductInstance> instances = service.findInstances(productId);
            JsonArray array = ProductInstanceCodec.encode(instances);
            return OxResponse.ok(array);
        } catch (IllegalArgumentException e) {
            return OxResponse.error(e);
        }
    }

    @GET
    @Path("/many")
    @Operation(
            summary = "get product instances",
            description = "get product instances"
    )
    @APIResponse(responseCode = "200", description = "success")
    public Response getManyByModel(
            @QueryParam("organization") String organization,
            @QueryParam("model") String model
    ) {
        logger.infov("getManyByModel: {0}/{1}", model);

        try {
            List<ProductInstance> instances = service.findInstancesByModel(organization, model);
            JsonArray array = ProductInstanceCodec.encode(instances);
            return OxResponse.ok(array);
        } catch (IllegalArgumentException e) {
            return OxResponse.error(e);
        }
    }

    @GET
    @Path("/all")
    @Operation(
            summary = "get all product instances",
            description = "get all product instances"
    )
    @APIResponse(responseCode = "200", description = "success")
    public Response getAll(
    ) {
        logger.infov("getAll");
        List<ProductInstance> instances = service.findAll();
        JsonArray array = ProductInstanceCodec.encode(instances);
        return OxResponse.ok(array);
    }
}
