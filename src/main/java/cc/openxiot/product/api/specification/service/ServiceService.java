package cc.openxiot.product.api.specification.service;

import cc.openxiot.product.db.specification.SpecificationEntity;
import cc.openxiot.product.db.specification.SpecificationRepository;
import cc.openxiot.product.db.specification.service.ServiceDefinitionEntity;
import cc.openxiot.product.db.specification.service.ServiceDefinitionMapper;
import cc.openxiot.common.exception.OxException;
import cc.openxiot.product.permission.NamespacePermission;
import cn.geekcity.xiot.spec.definition.ServiceDefinition;
import cn.geekcity.xiot.spec.definition.urn.ServiceType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;

@ApplicationScoped
public class ServiceService {

    @Inject
    SpecificationRepository repository;

    public void add(ServiceDefinition def, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = repository.findByNamespace(def.type().ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.namespace.organization);

        ServiceDefinitionEntity found = spec.services.get(def.type().name());
        if (found != null) {
            throw new IllegalArgumentException("service already exist!");
        }

        ServiceDefinitionEntity entity = ServiceDefinitionMapper.toEntity(def);
        spec.services.put(entity.code, entity);
        spec.update();
    }

    public void delete(ServiceType type, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = repository.findByNamespace(type.ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.namespace.organization);

        ServiceDefinitionEntity found = spec.services.get(type.name());
        if (found == null) {
            throw new IllegalArgumentException("service not found!");
        }

        spec.services.remove(type.name());
        spec.update();
    }

    public void update(ServiceDefinition def, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = repository.findByNamespace(def.type().ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.namespace.organization);

        ServiceDefinitionEntity found = spec.services.get(def.type().name());
        if (found == null) {
            throw new IllegalArgumentException("service not found!");
        }

        ServiceDefinitionEntity entity = ServiceDefinitionMapper.toEntity(def);
        spec.services.put(entity.code, entity);
        spec.update();
    }

    public ServiceDefinition find(ServiceType type) {
        SpecificationEntity spec = repository.findByNamespace(type.ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found!");
        }

        ServiceDefinitionEntity found = spec.services.get(type.name());
        if (found == null) {
            throw new IllegalArgumentException("service not found!");
        }

        return ServiceDefinitionMapper.toDefinition(spec, found);
    }

    public List<ServiceDefinition> findByNamespace(String ns) {
        SpecificationEntity spec = repository.findByNamespace(ns);
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found!");
        }

        return spec.services.values()
                .stream()
                .map(x -> ServiceDefinitionMapper.toDefinition(spec, x))
                .toList();
    }

    public List<ServiceDefinition> findAll() {
        List<ServiceDefinition> result = new ArrayList<>();

        List<SpecificationEntity> list = repository.listAll();

        for (SpecificationEntity spec : list) {
            if (spec.services !=null) {
                List<ServiceDefinition> services = spec.services.values()
                        .stream()
                        .map(x -> ServiceDefinitionMapper.toDefinition(spec, x))
                        .toList();

                result.addAll(services);
            }
        }

        return result;
    }
}