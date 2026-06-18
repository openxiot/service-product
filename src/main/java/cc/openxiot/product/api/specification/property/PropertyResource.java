package cc.openxiot.product.api.specification.property;

import cc.openxiot.product.exception.OxException;
import cc.openxiot.product.prepared.SpecificationPrepared;
import cc.openxiot.product.resource.AbstractResource;
import cc.openxiot.product.response.OxResponse;
import cc.openxiot.product.role.OxRole;
import cn.geekcity.xiot.spec.codec.vertx.definition.PropertyDefinitionCodec;
import cn.geekcity.xiot.spec.definition.FormatDefinition;
import cn.geekcity.xiot.spec.definition.PropertyDefinition;
import cn.geekcity.xiot.spec.definition.ServiceDefinition;
import cn.geekcity.xiot.spec.definition.urn.FormatType;
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

import java.io.IOException;
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
            service.add(PropertyDefinitionCodec.decode(item), this::checkManagerPermission);
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
            service.delete(PropertyType.parse(type), this::checkManagerPermission);
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
            service.update(PropertyDefinitionCodec.decode(item), this::checkManagerPermission);
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
            PropertyType t = PropertyType.parse(type);
            PropertyDefinition<?> def = prepared.contains(t.ns()) ? prepared.getProperty(t) : service.find(t);
            if (def == null) {
                return OxResponse.error("action not found");
            } else {
                return OxResponse.ok(PropertyDefinitionCodec.encode(def));
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
            List<PropertyDefinition<?>> list = prepared.contains(namespace) ?
                    prepared.getProperties(namespace) :
                    service.findByNamespace(namespace);
            List<JsonObject> array = list.stream().map(PropertyDefinitionCodec::encode).toList();
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
            List<PropertyDefinition<?>> list = service.findAll();
            list.addAll(prepared.getProperties(SpecificationPrepared.HOMEKIT_SPEC));
            List<JsonObject> array = list.stream().map(PropertyDefinitionCodec::encode).toList();
            return OxResponse.ok(new JsonArray(array));
        } catch (IOException e) {
            return OxResponse.error(e);
        }
    }
}
