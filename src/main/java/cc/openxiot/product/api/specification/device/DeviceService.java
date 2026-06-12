package cc.openxiot.product.api.specification.device;

import cc.openxiot.product.db.specification.SpecificationEntity;
import cc.openxiot.product.db.specification.SpecificationRepository;
import cc.openxiot.product.db.specification.device.DeviceDefinitionEntity;
import cc.openxiot.product.db.specification.device.DeviceDefinitionMapper;
import cc.openxiot.product.exception.OxException;
import cc.openxiot.product.resource.NamespacePermission;
import cn.geekcity.xiot.spec.definition.DeviceDefinition;
import cn.geekcity.xiot.spec.definition.urn.DeviceType;
import cn.geekcity.xiot.spec.definition.urn.DeviceType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class DeviceService {

    @Inject
    SpecificationRepository repository;

    public void add(DeviceDefinition def, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = repository.findByNamespace(def.type().ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.organization);

        DeviceDefinitionEntity found = spec.devices.get(def.type().name());
        if (found != null) {
            throw new IllegalArgumentException("device already exist!");
        }

        DeviceDefinitionEntity entity = DeviceDefinitionMapper.toEntity(def, null);
        spec.devices.put(entity.code, entity);
        spec.update();
    }

    public void delete(DeviceType type, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = repository.findByNamespace(type.ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.organization);

        DeviceDefinitionEntity found = spec.devices.get(type.name());
        if (found == null) {
            throw new IllegalArgumentException("device not found!");
        }

        spec.devices.remove(type.name());
        spec.update();
    }

    public void update(DeviceDefinition def, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = repository.findByNamespace(def.type().ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.organization);

        DeviceDefinitionEntity found = spec.devices.get(def.type().name());
        if (found == null) {
            throw new IllegalArgumentException("device not found!");
        }

        DeviceDefinitionEntity entity = DeviceDefinitionMapper.toEntity(def, null);
        spec.devices.put(entity.code, entity);
        spec.update();
    }

    public DeviceDefinition find(DeviceType type) {
        SpecificationEntity spec = repository.findByNamespace(type.ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found!");
        }

        DeviceDefinitionEntity found = spec.devices.get(type.name());
        if (found == null) {
            throw new IllegalArgumentException("device not found!");
        }

        return DeviceDefinitionMapper.toDefinition(spec.namespace.code, found);
    }

    public List<DeviceDefinition> findByNamespace(String ns) {
        SpecificationEntity spec = repository.findByNamespace(ns);
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found!");
        }

        return spec.devices.values()
                .stream()
                .map(x -> DeviceDefinitionMapper.toDefinition(spec.namespace.code, x))
                .collect(Collectors.toList());
    }

    public List<DeviceDefinition> findAll() {
        List<DeviceDefinition> result = new ArrayList<>();

        List<SpecificationEntity> list = repository.listAll();

        for (SpecificationEntity spec : list) {
            List<DeviceDefinition> devices = spec.devices.values()
                    .stream()
                    .map(x -> DeviceDefinitionMapper.toDefinition(spec.namespace.code, x))
                    .toList();

            result.addAll(devices);
        }

        return result;
    }
}