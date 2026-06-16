package cc.openxiot.product.api.specification.namespace;

import cc.openxiot.product.exception.OxException;
import cc.openxiot.product.resource.AbstractResource;
import cc.openxiot.product.response.OxResponse;
import cc.openxiot.product.role.OxRole;
import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.codec.vertx.definition.NamespaceDefinitionCodec;
import cn.geekcity.xiot.spec.definition.NamespaceDefinition;
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

@Path("/v1/spec/namespace")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Specification Namespaces", description = "Namespace API")
@RequestScoped
public class NamespaceResource extends AbstractResource {

    @Inject
    Logger logger;

    @Inject
    NamespaceService service;

    @POST
    @Path("/one")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    public Response add(JsonObject item) {
        logger.infov("add: {0}", item);

        try {
            NamespaceDefinition definition = NamespaceDefinitionCodec.decode(item);

            service.add(definition);

            return OxResponse.created();
        } catch (IllegalArgumentException e) {
            return OxResponse.error(e.getMessage());
        }
    }

    @DELETE
    @Path("/one")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    public Response delete(
            @QueryParam("namespace") String namespace
    ) {
        logger.infov("delete: {0}", namespace);

        try {
            service.delete(namespace, this::checkManagerPermission);

            return OxResponse.ok();
        } catch (OxException | IllegalArgumentException e) {
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
            service.update(NamespaceDefinitionCodec.decode(item), this::checkManagerPermission);
            return OxResponse.ok();
        } catch (OxException | IllegalArgumentException e) {
            return OxResponse.error(e.getMessage());
        }
    }

    @GET
    @Path("/one")
    public Response getOne(
            @QueryParam("namespace") String namespace
    ) {
        logger.infov("getOne: {0}", namespace);

        try {
            NamespaceDefinition def = service.find(namespace);
            if (def == null) {
                return OxResponse.error("namespace not found");
            } else {
                return OxResponse.ok(NamespaceDefinitionCodec.encode(def));
            }
        } catch (IllegalArgumentException e) {
            return OxResponse.error(e.getMessage());
        }
    }

    @GET
    @Path("/many/{organization}")
    public Response getMany(
            @PathParam("organization") String organization
    ) {
        logger.infov("getMany: {0}", organization);

        List<NamespaceDefinition> list = service.findByOrganization(organization);
        List<JsonObject> array = NamespaceDefinitionCodec.encode(list);
        return OxResponse.ok(new JsonArray(array));
    }

    @GET
    @Path("/visible/{organization}")
    public Response getVisible(
            @PathParam("organization") String organization
    ) {
        logger.infov("getVisible: {0}", organization);

        List<NamespaceDefinition> list = service.findVisible(organization);
        List<JsonObject> array = NamespaceDefinitionCodec.encode(list);
        return OxResponse.ok(new JsonArray(array));
    }

    @GET
    @Path("/public")
    public Response getAllPublic() {
        logger.infov("getAll");

        List<NamespaceDefinition> list = service.findPublic();
        List<JsonObject> array = NamespaceDefinitionCodec.encode(list);
        return OxResponse.ok(new JsonArray(array));
    }

    @GET
    @Path("/all")
    public Response getAll() {
        logger.infov("getAll");

        List<NamespaceDefinition> list = service.findAll();
        List<JsonObject> array = NamespaceDefinitionCodec.encode(list);
        return OxResponse.ok(new JsonArray(array));
    }
}
