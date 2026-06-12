package cc.openxiot.product.api.specification.event;

import cc.openxiot.product.exception.OxException;
import cc.openxiot.product.resource.AbstractResource;
import cc.openxiot.product.response.OxResponse;
import cc.openxiot.product.role.OxRole;
import cn.geekcity.xiot.spec.codec.vertx.definition.EventDefinitionCodec;
import cn.geekcity.xiot.spec.definition.EventDefinition;
import cn.geekcity.xiot.spec.definition.urn.EventType;
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

import java.util.List;

@Path("/v1/spec/event")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Specification Events", description = "Event API")
@RequestScoped
public class EventResource extends AbstractResource {

    @Inject
    Logger logger;

    @Inject
    EventService service;

    @POST
    @Path("/one")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    public Response add(
            JsonObject item
    ) {
        logger.infov("add: {0}", item);

        try {
            service.add(EventDefinitionCodec.decode(item), this::checkManagerPermission);
            return OxResponse.created();
        } catch (OxException e) {
            return OxResponse.error(e.getMessage());
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
            service.delete(EventType.parse(type), this::checkManagerPermission);
            return OxResponse.ok();
        } catch (OxException e) {
            return OxResponse.error(e.getMessage());
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
            service.update(EventDefinitionCodec.decode(item), this::checkManagerPermission);
            return OxResponse.ok();
        } catch (OxException e) {
            return OxResponse.error(e.getMessage());
        }
    }

    @GET
    @Path("/one/{type}")
    public Response getOne(
            @PathParam("type") String type
    ) {
        logger.infov("getOne, {0}", type);

        EventDefinition def = service.find(EventType.parse(type));
        if (def == null) {
            return OxResponse.error("action not found");
        } else {
            return OxResponse.ok(EventDefinitionCodec.encode(def));
        }
    }

    @GET
    @Path("/many/{namespace}")
    public Response getMany(
            @PathParam("namespace") String namespace
    ) {
        logger.infov("getMany: {0}", namespace);
        List<EventDefinition> list = service.findByNamespace(namespace);
        List<JsonObject> array = EventDefinitionCodec.encode(list);
        return OxResponse.ok(new JsonArray(array));
    }

    @GET
    @Path("/all")
    public Response getAll() {
        logger.infov("getAll");
        List<EventDefinition> list = service.findAll();
        List<JsonObject> array = EventDefinitionCodec.encode(list);
        return OxResponse.ok(new JsonArray(array));
    }
}
