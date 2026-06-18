package cc.openxiot.product.api.specification.format;

import cc.openxiot.common.exception.OxException;
import cc.openxiot.product.prepared.SpecificationPrepared;
import cc.openxiot.common.resource.AbstractResource;
import cc.openxiot.common.response.OxResponse;
import cc.openxiot.common.role.OxRole;
import cn.geekcity.xiot.spec.codec.vertx.definition.FormatDefinitionCodec;
import cn.geekcity.xiot.spec.definition.FormatDefinition;
import cn.geekcity.xiot.spec.definition.urn.FormatType;
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

@Path("/v1/spec/format")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Specification Formats", description = "Format API")
@RequestScoped
public class FormatResource extends AbstractResource {

    @Inject
    Logger logger;

    @Inject
    FormatService service;

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
            service.add(FormatDefinitionCodec.decode(item), this::checkManagerPermission);
            return OxResponse.created();
        } catch (OxException | IllegalArgumentException  e) {
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
            service.delete(FormatType.parse(type), this::checkManagerPermission);
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
            service.update(FormatDefinitionCodec.decode(item), this::checkManagerPermission);
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
            FormatType t = FormatType.parse(type);
            FormatDefinition def = prepared.contains(t.ns()) ? prepared.getFormat(t) : service.find(t);
            if (def == null) {
                return OxResponse.error("format not found");
            } else {
                return OxResponse.ok(FormatDefinitionCodec.encode(def));
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
            List<FormatDefinition> list = prepared.contains(namespace) ?
                    prepared.getFormats(namespace) :
                    service.findByNamespace(namespace);
            List<JsonObject> array = FormatDefinitionCodec.encode(list);
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
            List<FormatDefinition> list = service.findAll();
            list.addAll(prepared.getFormats(SpecificationPrepared.HOMEKIT_SPEC));
            list.addAll(prepared.getFormats(SpecificationPrepared.BLUETOOTH_SPEC));
            List<JsonObject> array = FormatDefinitionCodec.encode(list);
            return OxResponse.ok(new JsonArray(array));
        } catch (IOException e) {
            return OxResponse.error(e);
        }
    }
}
