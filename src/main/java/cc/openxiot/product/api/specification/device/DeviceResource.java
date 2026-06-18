package cc.openxiot.product.api.specification.device;

import cc.openxiot.common.exception.OxException;
import cc.openxiot.product.prepared.SpecificationPrepared;
import cc.openxiot.common.resource.AbstractResource;
import cc.openxiot.common.response.OxResponse;
import cc.openxiot.common.role.OxRole;
import cn.geekcity.xiot.spec.codec.vertx.definition.DeviceDefinitionCodec;
import cn.geekcity.xiot.spec.definition.DeviceDefinition;
import cn.geekcity.xiot.spec.definition.urn.DeviceType;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.io.IOException;
import java.util.List;

@Path("/v1/spec/device")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Specification Devices", description = "Device API")
@RequestScoped
public class DeviceResource extends AbstractResource {

    @Inject
    Logger logger;

    @Inject
    DeviceService service;

    @Inject
    SpecificationPrepared prepared;

    @POST
    @Path("/one")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    public Response add(
            JsonObject item
    ) {
        logger.infov("add: {0}", item);

        try {
            service.add(DeviceDefinitionCodec.decode(item), this::checkManagerPermission);
            return OxResponse.created();
        } catch (OxException | IllegalArgumentException e) {
            return OxResponse.error(e);
        }
    }

    @DELETE
    @Path("/one/{type}")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    public Response delete(
            @PathParam("type") String type
    ) {
        logger.infov("delete, {0}", type);

        try {
            service.delete(DeviceType.parse(type), this::checkManagerPermission);
            return OxResponse.ok();
        } catch (OxException | IllegalArgumentException e) {
            return OxResponse.error(e);
        }
    }

    @PUT
    @Path("/one")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    public Response update(
            JsonObject item
    ) {
        logger.infov("update: {0}", item);

        try {
            service.update(DeviceDefinitionCodec.decode(item), this::checkManagerPermission);
            return OxResponse.ok();
        } catch (OxException | IllegalArgumentException e) {
            return OxResponse.error(e);
        }
    }

    @GET
    @Path("/one/{type}")
    public Response getOne(
            @PathParam("type") String type
    ) {
        logger.infov("getOne, {0}", type);

        try {
            DeviceType t = DeviceType.parse(type);
            DeviceDefinition def = prepared.contains(t.ns()) ? prepared.getDevice(t) : service.find(t);
            if (def == null) {
                return OxResponse.error("action not found");
            } else {
                return OxResponse.ok(DeviceDefinitionCodec.encode(def));
            }
        } catch (IllegalArgumentException | IOException e) {
            return OxResponse.error(e);
        }
    }

    @GET
    @Path("/many/{namespace}")
    public Response getMany(
            @PathParam("namespace") String namespace
    ) {
        logger.infov("getMany: {0}", namespace);

        try {
            List<DeviceDefinition> list = prepared.contains(namespace) ?
                    prepared.getDevices(namespace) :
                    service.findByNamespace(namespace);
            List<JsonObject> array = DeviceDefinitionCodec.encode(list);
            return OxResponse.ok(new JsonArray(array));
        } catch (IllegalArgumentException | IOException e) {
            return OxResponse.error(e);
        }
    }

    @GET
    @Path("/all")
    public Response getAll() {
        logger.infov("getAll");
        try {
            List<DeviceDefinition> list = service.findAll();
            list.addAll(prepared.getDevices(SpecificationPrepared.HOMEKIT_SPEC));
            list.addAll(prepared.getDevices(SpecificationPrepared.BLUETOOTH_SPEC));
            List<JsonObject> array = DeviceDefinitionCodec.encode(list);
            return OxResponse.ok(new JsonArray(array));
        } catch (IOException e) {
            return OxResponse.error(e);
        }
    }
}
