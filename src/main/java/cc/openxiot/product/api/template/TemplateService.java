package cc.openxiot.product.api.template;

import cc.openxiot.product.db.specification.SpecificationEntity;
import cc.openxiot.product.db.specification.SpecificationRepository;
import cc.openxiot.product.db.template.TemplateEntity;
import cc.openxiot.product.db.template.TemplateMapper;
import cc.openxiot.product.db.template.TemplateRepository;
import cc.openxiot.common.exception.OxException;
import cc.openxiot.product.permission.NamespacePermission;
import cn.geekcity.xiot.spec.codec.vertx.template.DeviceTemplateCodec;
import cn.geekcity.xiot.spec.definition.urn.DeviceType;
import cn.geekcity.xiot.spec.template.DeviceTemplate;
import cn.geekcity.xiot.spec.template.TemplateSummary;
import io.vertx.core.json.JsonObject;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class TemplateService {

    @Inject
    SpecificationRepository specification;

    @Inject
    TemplateRepository template;

    public void add(DeviceTemplate device, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = specification.findByNamespace(device.type().ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.namespace.organization);

        TemplateEntity found = template.findBy(device.type().ns(), device.type().organization(), device.type().model(), device.type().version());
        if (found != null) {
            throw new IllegalArgumentException("template already exist!");
        }

        TemplateEntity entity = TemplateMapper.toEntity(device);
        template.persist(entity);
        // persist 后 entity 自动获取 ID 并写入 MongoDB
    }

    public void delete(DeviceType type, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = specification.findByNamespace(type.ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.namespace.organization);

        TemplateEntity found = template.findBy(type.ns(), type.organization(), type.model(), type.version());
        if (found == null) {
            throw new IllegalArgumentException("template not found!");
        }

        found.delete();
    }

    public void update(DeviceTemplate device, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = specification.findByNamespace(device.type().ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.namespace.organization);

        TemplateEntity found = template.findBy(device.type().ns(), device.type().organization(), device.type().model(), device.type().version());
        if (found == null) {
            throw new IllegalArgumentException("template not found!");
        }

        found.content = DeviceTemplateCodec.encode(device).toString();
        found.lifecycle = device.lifecycle().toString();
        found.description = device.description();
        found.update();
    }

    public DeviceTemplate find(DeviceType type) {
        TemplateEntity found = template.findBy(type.ns(), type.organization(), type.model(), type.version());
        if (found == null) {
            throw new IllegalArgumentException("template not found!");
        }

        return DeviceTemplateCodec.decode(new JsonObject(found.content));
    }

    public List<TemplateSummary> getSummaryByNamespace(String ns) {
        return template.getSummaryByNamespace(ns)
                .stream()
                .map(TemplateMapper::toSummary)
                .collect(Collectors.toList());
    }

    public List<DeviceTemplate> findByOrganization(String org) {
        return template.findByOrganization(org)
                .stream()
                .map(x -> DeviceTemplateCodec.decode(new JsonObject(x.content)))
                .collect(Collectors.toList());
    }

    public List<TemplateSummary> getAllTemplate() {
        return template.getAllSummary()
                .stream()
                .map(TemplateMapper::toSummary)
                .collect(Collectors.toList());
    }
}