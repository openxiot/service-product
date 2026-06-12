package cc.openxiot.product.api.specification.namespace;

import cc.openxiot.product.db.history.History;
import cc.openxiot.product.exception.OxException;
import cc.openxiot.product.resource.ResourceBase;
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
import org.eclipse.microprofile.jwt.JsonWebToken;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/v1/spec/namespace")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Specification Namespaces", description = "Namespace API")
@RequestScoped
public class NamespaceResource extends ResourceBase {

    @Inject
    Logger logger;

    @Inject
    JsonWebToken jwt;

    @Inject
    NamespaceService service;

    @POST
    @Path("/one/{organization}")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    @Operation(summary = "add", description = "add namespace")
    @APIResponse(responseCode = "200", description = "success")
    @APIResponse(responseCode = "401", description = "unauthorized")
    @APIResponse(responseCode = "403", description = "forbidden")
    public Response add(
            @PathParam("organization") String organization,
            JsonObject item
    ) {
        logger.infov("add: {0}", item);

        try {
            NamespaceDefinition definition = NamespaceDefinitionCodec.decode(item);

            Creator creator = getCreator(jwt, organization);

            service.add(organization, definition, creator);

            History.addDeveloper(organization, creator.id(), "ADD", "NamespaceDefinition", item.toString());

            return OxResponse.created();
        } catch (OxException e) {
            return OxResponse.error(e.getMessage());
        }
    }

    @DELETE
    @Path("/one/{organization}")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    public Response delete(
            @PathParam("organization") String organization,
            @QueryParam("namespace") String namespace
    ) {
        logger.infov("delete, {0}/{1}", organization, namespace);

        try {
            checkManagerPermission(jwt, organization);

            NamespaceDefinition def = service.find(organization, namespace);
            if (def == null) {
                return OxResponse.error("namespace not found");
            }

            service.delete(organization, namespace);

            History.addDeveloper(organization, jwt.getName(), "DELETE", "NamespaceDefinition", NamespaceDefinitionCodec.encode(def).toString());

            return OxResponse.ok();
        } catch (OxException e) {
            return OxResponse.error(e.getMessage());
        }
    }

    @PUT
    @Path("/one/{organization}")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    public Response update(
            @PathParam("organization") String organization,
            JsonObject item
    ) {
        logger.infov("update, {0}", item);

        try {
            NamespaceDefinition def = NamespaceDefinitionCodec.decode(item);

            checkManagerPermission(jwt, organization);

            service.update(organization, def);

            History.addDeveloper(organization, jwt.getName(), "UPDATE", "NamespaceDefinition", item.toString());

            return OxResponse.ok();
        } catch (OxException e) {
            return OxResponse.error(e.getMessage());
        }
    }

    @GET
    @Path("/one/{organization}")
    @Operation(summary = "get one namespace", description = "get one namespace")
    @APIResponse(responseCode = "200", description = "success")
    public Response getOne(
            @PathParam("organization") String organization,
            @QueryParam("namespace") String namespace
    ) {
        logger.infov("getOne, {0}/{1}", organization, namespace);

        NamespaceDefinition def = service.find(organization, namespace);
        if (def == null) {
            return OxResponse.error("namespace not found");
        } else {
            return OxResponse.ok(NamespaceDefinitionCodec.encode(def));
        }
    }

    @GET
    @Path("/all/{organization}")
    @Operation(summary = "get all namespace", description = "get all namespace")
    @APIResponse(responseCode = "200", description = "success")
    public Response getAll(
            @PathParam("organization") String organization
    ) {
        logger.infov("getAll, organization: {0}", organization);

        List<NamespaceDefinition> list;
        if (organization == null || organization.isBlank()) {
            list = service.findAll();
        } else {
            list = service.findByOrganization(organization);
        }

        List<JsonObject> array = NamespaceDefinitionCodec.encode(list);

        return OxResponse.ok(new JsonArray(array));
    }
}
