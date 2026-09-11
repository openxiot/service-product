package cc.openxiot.product.api.product.controller;

import cc.openxiot.account.db.history.History;
import cc.openxiot.common.exception.OxException;
import cc.openxiot.common.resource.AbstractResource;
import cc.openxiot.common.response.OxResponse;
import cc.openxiot.common.role.OxRole;
import cc.openxiot.product.db.product.controller.ProductControllerMapper;
import cn.geekcity.xiot.spec.by.Creator;
import cn.geekcity.xiot.spec.by.Updater;
import cn.geekcity.xiot.spec.codec.vertx.product.controller.ProductControllerCodec;
import cn.geekcity.xiot.spec.definition.urn.Urn;
import cn.geekcity.xiot.spec.lifecycle.Lifecycle;
import cn.geekcity.xiot.spec.product.controller.ProductController;
import io.vertx.core.json.JsonArray;
import io.vertx.core.json.JsonObject;
import jakarta.annotation.security.RolesAllowed;
import jakarta.enterprise.context.RequestScoped;
import jakarta.inject.Inject;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;
import org.eclipse.microprofile.openapi.annotations.Operation;
import org.eclipse.microprofile.openapi.annotations.responses.APIResponse;
import org.eclipse.microprofile.openapi.annotations.tags.Tag;
import org.jboss.logging.Logger;

import java.util.List;

@Path("/v1/product/controller")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Tag(name = "Product Controller", description = "Product Controller API")
@RequestScoped
public class ProductControllerResource extends AbstractResource {

    @Inject
    Logger logger;

    @Inject
    ProductControllerService service;

    @POST
    @Path("/one")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    @Operation(summary = "add", description = "add product controller")
    @APIResponse(responseCode = "200", description = "success")
    @APIResponse(responseCode = "401", description = "unauthorized")
    @APIResponse(responseCode = "403", description = "forbidden")
    public Response add(JsonObject item) {
        logger.infov("add: {0}", item);

        try {
            ProductController controller = decode(item);

            Creator creator = getCreator(controller.instance().organization());
            controller.creator(creator);
            controller.updater(new Updater().id(creator.id()).name(creator.name()).timestamp(creator.timestamp()));

            if (controller.lifecycle() == null || controller.lifecycle() == Lifecycle.UNDEFINED) {
                controller.lifecycle(Lifecycle.DEVELOPMENT);
            }

            service.add(controller);

            JsonObject object = ProductControllerCodec.encode(controller);

            History.addDeveloper(controller.instance().organization(), creator.id(), "ADD", "ProductController", object.toString());

            return OxResponse.ok(object);
        } catch (OxException | IllegalArgumentException e) {
            return OxResponse.error(e);
        }
    }

    @DELETE
    @Path("/one")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    public Response delete(
            @QueryParam("instance") String instance,
            @QueryParam("category") String category,
            @QueryParam("versionCode") int versionCode
    ) {
        logger.infov("delete, {0}/{1}/{2}", instance, category, versionCode);

        try {
            Urn urn = toInstance(instance);

            checkManagerPermission(urn.organization());

            service.delete(urn, category, versionCode);

            History.addDeveloper(urn.organization(), getAccountId(), "DELETE", "ProductController", instance);

            return OxResponse.ok();
        } catch (OxException | IllegalArgumentException e) {
            return OxResponse.error(e);
        }
    }

    @PUT
    @Path("/one")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    public Response update(JsonObject item) {
        logger.infov("update, {0}", item);

        try {
            ProductController controller = decode(item);

            checkManagerPermission(controller.instance().organization());

            controller.updater(getUpdater(controller.instance().organization()));

            service.update(controller);

            History.addDeveloper(controller.instance().organization(), getAccountId(), "UPDATE", "ProductController", item.toString());

            return OxResponse.ok();
        } catch (OxException | IllegalArgumentException e) {
            return OxResponse.error(e);
        }
    }

    @PUT
    @Path("/one/lifecycle/{lifecycle}")
    @RolesAllowed({OxRole.DEVELOPER, OxRole.OPERATOR, OxRole.ADMIN})
    public Response updateLifecycle(
            @PathParam("lifecycle") String lifecycle,
            JsonObject item
    ) {
        logger.infov("updateLifecycle, {0} => {1}", item, lifecycle);

        try {
            Urn urn = toInstance(item.getString("instance"));
            String category = requireCategory(item.getString("category"));
            int versionCode = requireVersionCode(item.getJsonObject("version"));

            checkManagerPermission(urn.organization());

            Updater updater = getUpdater(urn.organization());

            service.updateLifecycle(urn, category, versionCode, Lifecycle.of(lifecycle), updater);

            JsonObject detail = new JsonObject()
                    .put("instance", urn.toString())
                    .put("category", category)
                    .put("versionCode", versionCode)
                    .put("lifecycle", lifecycle);

            History.addDeveloper(urn.organization(), getAccountId(), "UPDATE", "ProductController.Lifecycle", detail.toString());

            return OxResponse.ok();
        } catch (OxException | IllegalArgumentException e) {
            return OxResponse.error(e);
        }
    }

    @GET
    @Path("/one")
    public Response getOne(
            @QueryParam("instance") String instance,
            @QueryParam("category") String category,
            @QueryParam("versionCode") int versionCode
    ) {
        logger.infov("getOne, {0}/{1}/{2}", instance, category, versionCode);

        try {
            Urn urn = toInstance(instance);

            ProductController controller = service.findOne(urn, category, versionCode);
            if (controller == null) {
                return OxResponse.error("product controller not found");
            }

            return OxResponse.ok(ProductControllerCodec.encode(controller));
        } catch (IllegalArgumentException e) {
            return OxResponse.error(e);
        }
    }

    @GET
    @Path("/many")
    @Operation(summary = "getMany", description = "get product controllers by productId or by product instance urn")
    public Response getMany(
            @QueryParam("productId") String productId,
            @QueryParam("instance") String instance
    ) {
        logger.infov("getMany, productId={0}, instance={1}", productId, instance);

        try {
            List<ProductController> controllers;

            if (instance != null && !instance.isBlank()) {
                controllers = service.findByInstance(toInstance(instance));
            } else if (productId != null && !productId.isBlank()) {
                controllers = service.findByProduct(productId);
            } else {
                return OxResponse.error("productId or instance is required");
            }

            JsonArray array = encode(controllers);

            return OxResponse.ok(array);
        } catch (IllegalArgumentException e) {
            return OxResponse.error(e);
        }
    }

    @GET
    @Path("/all")
    @Operation(summary = "getAll", description = "get all product controllers")
    public Response getAll() {
        logger.infov("getAll");

        List<ProductController> controllers = service.findAll();
        JsonArray array = encode(controllers);

        return OxResponse.ok(array);
    }

    // ------------------------------------------------------------------

    // ProductControllerCodec 只有单对象的 encode，没有 encode(List) 重载
    private static JsonArray encode(List<ProductController> controllers) {
        JsonArray array = new JsonArray();

        for (ProductController controller : controllers) {
            array.add(ProductControllerCodec.encode(controller));
        }

        return array;
    }

    // ProductControllerCodec 对 instance / version / version.code 没有空值保护，
    // 缺字段会在 decode 时抛 NPE，这里先做一次校验，转成可读的错误
    private static ProductController decode(JsonObject item) {
        toInstance(item.getString("instance"));

        requireCategory(item.getString("category"));
        requireVersionCode(item.getJsonObject("version"));

        return ProductControllerCodec.decode(item);
    }

    private static Urn toInstance(String instance) {
        if (instance == null || instance.isBlank()) {
            throw new IllegalArgumentException("instance is empty");
        }

        Urn urn = ProductControllerMapper.of(instance);

        if (urn.invalid()) {
            throw new IllegalArgumentException("invalid instance: " + instance);
        }

        // 只有 xiot 风格的 urn 才带 organization / model，才能定位到产品
        if (urn.organization() == null || urn.model() == null) {
            throw new IllegalArgumentException("instance is not a product instance urn: " + instance);
        }

        return urn;
    }

    private static String requireCategory(String category) {
        if (category == null || category.isBlank()) {
            throw new IllegalArgumentException("category is empty");
        }

        return category;
    }

    private static int requireVersionCode(JsonObject version) {
        if (version == null || version.getInteger("code") == null) {
            throw new IllegalArgumentException("version.code is empty");
        }

        return version.getInteger("code");
    }
}
