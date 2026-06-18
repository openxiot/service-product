package cc.openxiot.product.api.specification.unit;

import cc.openxiot.product.exception.OxException;
import cc.openxiot.product.prepared.SpecificationPrepared;
import cc.openxiot.product.resource.AbstractResource;
import cc.openxiot.product.response.OxResponse;
import cc.openxiot.product.role.OxRole;
import cn.geekcity.xiot.spec.codec.vertx.definition.UnitDefinitionCodec;
import cn.geekcity.xiot.spec.definition.ActionDefinition;
import cn.geekcity.xiot.spec.definition.ServiceDefinition;
import cn.geekcity.xiot.spec.definition.UnitDefinition;
import cn.geekcity.xiot.spec.definition.urn.ServiceType;
import cn.geekcity.xiot.spec.definition.urn.UnitType;
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

@Path("/v1/spec/unit")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Specification Units", description = "Unit API")
@RequestScoped
public class UnitResource extends AbstractResource {

    @Inject
    Logger logger;

    @Inject
    UnitService service;

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
            service.add(UnitDefinitionCodec.decode(item), this::checkManagerPermission);
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
            service.delete(UnitType.parse(type), this::checkManagerPermission);
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
            service.update(UnitDefinitionCodec.decode(item), this::checkManagerPermission);
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
            UnitType t = UnitType.parse(type);
            UnitDefinition def = prepared.contains(t.ns()) ? prepared.getUnit(t) : service.find(t);
            if (def == null) {
                return OxResponse.error("action not found");
            } else {
                return OxResponse.ok(UnitDefinitionCodec.encode(def));
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
            List<UnitDefinition> list = prepared.contains(namespace) ?
                    prepared.getUnits(namespace) :
                    service.findByNamespace(namespace);
            List<JsonObject> array = UnitDefinitionCodec.encode(list);
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
            List<UnitDefinition> list = service.findAll();
            list.addAll(prepared.getUnits(SpecificationPrepared.HOMEKIT_SPEC));
            List<JsonObject> array = UnitDefinitionCodec.encode(list);
            return OxResponse.ok(new JsonArray(array));
        } catch (IOException e) {
            return OxResponse.error(e);
        }
    }
}
