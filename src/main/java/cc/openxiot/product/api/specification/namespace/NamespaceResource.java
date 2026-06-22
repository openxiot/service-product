package cc.openxiot.product.api.specification.namespace;

import cc.openxiot.common.exception.OxException;
import cc.openxiot.product.prepared.SpecificationPrepared;
import cc.openxiot.common.resource.AbstractResource;
import cc.openxiot.common.response.OxResponse;
import cc.openxiot.common.role.OxRole;
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

import java.io.IOException;
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

    @Inject
    SpecificationPrepared prepared;

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
            return OxResponse.error(e);
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
            service.update(NamespaceDefinitionCodec.decode(item), this::checkManagerPermission);
            return OxResponse.ok();
        } catch (OxException | IllegalArgumentException e) {
            return OxResponse.error(e);
        }
    }

    @GET
    @Path("/one")
    public Response getOne(
            @QueryParam("namespace") String namespace
    ) {
        logger.infov("getOne: {0}", namespace);

        try {
            NamespaceDefinition def = prepared.contains(namespace) ?
                    prepared.getNamespaceDefinition(namespace) :
                    service.find(namespace);

            if (def == null) {
                return OxResponse.error("namespace not found");
            } else {
                return OxResponse.ok(NamespaceDefinitionCodec.encode(def));
            }
        } catch (IllegalArgumentException | IOException e) {
            return OxResponse.error(e);
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

        try {
            List<NamespaceDefinition> list = service.findVisible(organization);

            NamespaceDefinition homekit = prepared.getNamespaceDefinition(SpecificationPrepared.HOMEKIT_SPEC);
            if (homekit != null) {
                list.add(homekit);
            }

            NamespaceDefinition bluetooth = prepared.getNamespaceDefinition(SpecificationPrepared.BLUETOOTH_SPEC);
            if (bluetooth != null) {
                list.add(bluetooth);
            }

            List<JsonObject> array = NamespaceDefinitionCodec.encode(list);
            return OxResponse.ok(new JsonArray(array));
        } catch (IOException e) {
            return OxResponse.error(e);
        }
    }

    @GET
    @Path("/public")
    public Response getPublic() {
        logger.infov("getPublic");

        try {
            List<NamespaceDefinition> list = service.findPublic();

            NamespaceDefinition homekit = prepared.getNamespaceDefinition(SpecificationPrepared.HOMEKIT_SPEC);
            if (homekit != null) {
                list.add(homekit);
            }

            NamespaceDefinition bluetooth = prepared.getNamespaceDefinition(SpecificationPrepared.BLUETOOTH_SPEC);
            if (bluetooth != null) {
                list.add(bluetooth);
            }

            List<JsonObject> array = NamespaceDefinitionCodec.encode(list);
            return OxResponse.ok(new JsonArray(array));
        } catch (IOException e) {
            return OxResponse.error(e);
        }
    }

    @GET
    @Path("/all")
    public Response getAll() {
        logger.infov("getAll");

        try {
            List<NamespaceDefinition> list = service.findAll();

            NamespaceDefinition homekit = prepared.getNamespaceDefinition(SpecificationPrepared.HOMEKIT_SPEC);
            if (homekit != null) {
                list.add(homekit);
            }

            NamespaceDefinition bluetooth = prepared.getNamespaceDefinition(SpecificationPrepared.BLUETOOTH_SPEC);
            if (bluetooth != null) {
                list.add(bluetooth);
            }

            List<JsonObject> array = NamespaceDefinitionCodec.encode(list);
            return OxResponse.ok(new JsonArray(array));
        } catch (IOException e) {
            return OxResponse.error(e);
        }
    }
}
