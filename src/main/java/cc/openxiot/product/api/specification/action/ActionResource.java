package cc.openxiot.product.api.specification.action;

import cc.openxiot.product.db.history.History;
import cc.openxiot.product.db.specification.SpecificationEntity;
import cc.openxiot.product.exception.OxException;
import cc.openxiot.product.resource.ResourceBase;
import cc.openxiot.product.response.OxResponse;
import cc.openxiot.product.role.OxRole;
import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.codec.vertx.definition.ActionDefinitionCodec;
import cn.geekcity.xiot.spec.definition.ActionDefinition;
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

@Path("/v1/spec/action")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Specification Actions", description = "Action API")
@RequestScoped
public class ActionResource extends ResourceBase {

    @Inject
    Logger logger;

    @Inject
    JsonWebToken jwt;

    @Inject
    ActionService service;

    @POST
    @Path("/one")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    @Operation(summary = "add", description = "add namespace")
    @APIResponse(responseCode = "200", description = "success")
    @APIResponse(responseCode = "401", description = "unauthorized")
    @APIResponse(responseCode = "403", description = "forbidden")
    public Response add(
            JsonObject item
    ) {
        logger.infov("add: {0}", item);

        try {
            ActionDefinition definition = ActionDefinitionCodec.decode(item);

            SpecificationEntity spec = service.repository.findByNamespace(definition.type().ns());
            if (spec == null) {
                throw new IllegalArgumentException("namespace not found");
            }

            checkManagerPermission(jwt, spec.organization);

            Creator creator = getCreator(jwt, spec.organization);

            service.add(definition, creator);

            History.addDeveloper(spec.organization, creator.id(), "ADD", "ActionDefinition", item.toString());

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

            ActionDefinition def = service.find(organization, namespace);
            if (def == null) {
                return OxResponse.error("namespace not found");
            }

            service.delete(organization, namespace);

            History.addDeveloper(organizationId, jwt.getName(), "DELETE", "ProductBasic", ActionDefinitionCodec.encode(def).toString());

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
            ActionDefinition def = ActionDefinitionCodec.decode(item);

            checkManagerPermission(jwt, organization);

            service.update(organization, def);

            History.addDeveloper(organization, jwt.getName(), "UPDATE", "Item", item.toString());

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
            @QueryParam("organizationId") String organizationId,
            @QueryParam("namespace") String namespace
    ) {
        logger.infov("getOne, {0}/{1}", organizationId, namespace);

        ActionDefinition def = service.find(organizationId, namespace);
        if (def == null) {
            return OxResponse.error("namespace not found");
        } else {
            return OxResponse.ok(ActionDefinitionCodec.encode(def));
        }
    }

    @GET
    @Path("/all")
    @Operation(summary = "get all namespace", description = "get all namespace")
    @APIResponse(responseCode = "200", description = "success")
    public Response getAll(
            @QueryParam("organizationId") String organizationId
    ) {
        logger.infov("getAll, organizationId: {0}", organizationId);

        List<ActionDefinition> list;
        if (organizationId == null || organizationId.isBlank()) {
            list = service.findAll();
        } else {
            list = service.findByOrganization(organizationId);
        }

        List<JsonObject> array = ActionDefinitionCodec.encode(list);

        return OxResponse.ok(new JsonArray(array));
    }
}
