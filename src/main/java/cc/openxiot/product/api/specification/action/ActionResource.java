package cc.openxiot.product.api.specification.action;

import cc.openxiot.product.exception.OxException;
import cc.openxiot.product.prepared.SpecificationPrepared;
import cc.openxiot.product.resource.AbstractResource;
import cc.openxiot.product.response.OxResponse;
import cc.openxiot.product.role.OxRole;
import cn.geekcity.xiot.spec.codec.vertx.definition.ActionDefinitionCodec;
import cn.geekcity.xiot.spec.definition.ActionDefinition;
import cn.geekcity.xiot.spec.definition.urn.ActionType;
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

@Path("/v1/spec/action")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Specification Actions", description = "Action API")
@RequestScoped
public class ActionResource extends AbstractResource {

    @Inject
    Logger logger;

    @Inject
    ActionService service;

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
            service.add(ActionDefinitionCodec.decode(item), this::checkManagerPermission);
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
            service.delete(ActionType.parse(type), this::checkManagerPermission);
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
            service.update(ActionDefinitionCodec.decode(item), this::checkManagerPermission);
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
            ActionType t = ActionType.parse(type);
            ActionDefinition def = prepared.contains(t.ns()) ? prepared.getAction(t) : service.find(t);
            if (def == null) {
                return OxResponse.error("action not found");
            } else {
                return OxResponse.ok(ActionDefinitionCodec.encode(def));
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
            List<ActionDefinition> list = prepared.contains(namespace) ?
                    prepared.getActions(namespace) :
                    service.findByNamespace(namespace);
            List<JsonObject> array = ActionDefinitionCodec.encode(list);
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
            List<ActionDefinition> list = service.findAll();
            list.addAll(prepared.getActions(SpecificationPrepared.HOMEKIT_SPEC));
            List<JsonObject> array = ActionDefinitionCodec.encode(list);
            return OxResponse.ok(new JsonArray(array));
        } catch (IOException e) {
            return OxResponse.error(e);
        }
    }
}
