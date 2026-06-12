package cc.openxiot.product.api.specification.property;

import cc.openxiot.product.exception.OxException;
import cc.openxiot.product.resource.AbstractResource;
import cc.openxiot.product.response.OxResponse;
import cc.openxiot.product.role.OxRole;
import cn.geekcity.xiot.spec.codec.vertx.definition.PropertyDefinitionCodec;
import cn.geekcity.xiot.spec.definition.PropertyDefinition;
import cn.geekcity.xiot.spec.definition.urn.PropertyType;
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

@Path("/v1/spec/property")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Specification Properties", description = "Property API")
@RequestScoped
public class PropertyResource extends AbstractResource {

    @Inject
    Logger logger;

    @Inject
    PropertyService service;

    @POST
    @Path("/one")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    public Response add(
            JsonObject item
    ) {
        logger.infov("add: {0}", item);

        try {
            service.add(PropertyDefinitionCodec.decode(item), this::checkManagerPermission);
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
            service.delete(PropertyType.parse(type), this::checkManagerPermission);
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
            service.update(PropertyDefinitionCodec.decode(item), this::checkManagerPermission);
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

        PropertyDefinition def = service.find(PropertyType.parse(type));
        if (def == null) {
            return OxResponse.error("action not found");
        } else {
            return OxResponse.ok(PropertyDefinitionCodec.encode(def));
        }
    }

    @GET
    @Path("/many/{namespace}")
    public Response getMany(
            @PathParam("namespace") String namespace
    ) {
        logger.infov("getMany: {0}", namespace);
        List<PropertyDefinition<?>> list = service.findByNamespace(namespace);
        List<JsonObject> array = list.stream().map(PropertyDefinitionCodec::encode).toList();
        return OxResponse.ok(new JsonArray(array));
    }

    @GET
    @Path("/all")
    public Response getAll() {
        logger.infov("getAll");
        List<PropertyDefinition<?>> list = service.findAll();
        List<JsonObject> array = list.stream().map(PropertyDefinitionCodec::encode).toList();
        return OxResponse.ok(new JsonArray(array));
    }
}
