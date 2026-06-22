package cc.openxiot.product.api.template;

import cc.openxiot.common.exception.OxException;
import cc.openxiot.common.resource.AbstractResource;
import cc.openxiot.common.response.OxResponse;
import cc.openxiot.common.role.OxRole;
import cc.openxiot.product.prepared.TemplatePrepared;
import cn.geekcity.xiot.spec.codec.vertx.template.DeviceTemplateCodec;
import cn.geekcity.xiot.spec.codec.vertx.template.TemplateSummaryCodec;
import cn.geekcity.xiot.spec.definition.urn.DeviceType;
import cn.geekcity.xiot.spec.template.DeviceTemplate;
import cn.geekcity.xiot.spec.template.TemplateSummary;
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

@Path("/v1/template")
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Templates", description = "Template API")
@RequestScoped
public class TemplateResource extends AbstractResource {

    @Inject
    Logger logger;

    @Inject
    TemplateService service;

    @Inject
    TemplatePrepared prepared;

    @POST
    @Path("/one")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    public Response add(
            JsonObject item
    ) {
        logger.infov("add: {0}", item);

        try {
            service.add(DeviceTemplateCodec.decode(item), this::checkManagerPermission);
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
            service.delete(DeviceType.parse(type), this::checkManagerPermission);
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
            service.update(DeviceTemplateCodec.decode(item), this::checkManagerPermission);
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
            DeviceType t = DeviceType.parse(type);
            DeviceTemplate template = prepared.contains(t.ns()) ? prepared.getTemplate(t) : service.find(t);
            if (template == null) {
                return OxResponse.error("template not found");
            } else {
                return OxResponse.ok(DeviceTemplateCodec.encode(template));
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
            List<TemplateSummary> list = service.getSummaryByNamespace(namespace);
            list.addAll(prepared.getTemplates(namespace));
            return OxResponse.ok(TemplateSummaryCodec.encode(list));
        } catch (Exception e) {
            e.printStackTrace();
            return OxResponse.error(e);
        }
    }

    @GET
    @Path("/all")
    public Response getAll() {
        logger.infov("getAll");

        try {
            List<TemplateSummary> list = service.getAllTemplate();
            list.addAll(prepared.getAllTemplate());
            return OxResponse.ok(TemplateSummaryCodec.encode(list));
        } catch (Exception e) {
            return OxResponse.error(e);
        }
    }
}
