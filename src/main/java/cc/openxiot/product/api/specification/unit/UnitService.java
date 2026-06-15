package cc.openxiot.product.api.specification.unit;

import cc.openxiot.product.db.specification.SpecificationEntity;
import cc.openxiot.product.db.specification.SpecificationRepository;
import cc.openxiot.product.db.specification.unit.UnitDefinitionEntity;
import cc.openxiot.product.db.specification.unit.UnitDefinitionMapper;
import cc.openxiot.product.exception.OxException;
import cc.openxiot.product.resource.NamespacePermission;
import cn.geekcity.xiot.spec.definition.UnitDefinition;
import cn.geekcity.xiot.spec.definition.urn.UnitType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class UnitService {

    @Inject
    SpecificationRepository repository;

    public void add(UnitDefinition def, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = repository.findByNamespace(def.type().ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.namespace.organization);

        UnitDefinitionEntity found = spec.units.get(def.type().name());
        if (found != null) {
            throw new IllegalArgumentException("unit already exist!");
        }

        UnitDefinitionEntity entity = UnitDefinitionMapper.toEntity(def, null);
        spec.units.put(entity.code, entity);
        spec.update();
    }

    public void delete(UnitType type, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = repository.findByNamespace(type.ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.namespace.organization);

        UnitDefinitionEntity found = spec.units.get(type.name());
        if (found == null) {
            throw new IllegalArgumentException("unit not found!");
        }

        spec.units.remove(type.name());
        spec.update();
    }

    public void update(UnitDefinition def, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = repository.findByNamespace(def.type().ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.namespace.organization);

        UnitDefinitionEntity found = spec.units.get(def.type().name());
        if (found == null) {
            throw new IllegalArgumentException("unit not found!");
        }

        UnitDefinitionEntity entity = UnitDefinitionMapper.toEntity(def, null);
        spec.units.put(entity.code, entity);
        spec.update();
    }

    public UnitDefinition find(UnitType type) {
        SpecificationEntity spec = repository.findByNamespace(type.ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found!");
        }

        UnitDefinitionEntity found = spec.units.get(type.name());
        if (found == null) {
            throw new IllegalArgumentException("unit not found!");
        }

        return UnitDefinitionMapper.toDefinition(spec.namespace.code, found);
    }

    public List<UnitDefinition> findByNamespace(String ns) {
        SpecificationEntity spec = repository.findByNamespace(ns);
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found!");
        }

        return spec.units.values()
                .stream()
                .map(x -> UnitDefinitionMapper.toDefinition(spec.namespace.code, x))
                .collect(Collectors.toList());
    }

    public List<UnitDefinition> findAll() {
        List<UnitDefinition> result = new ArrayList<>();

        List<SpecificationEntity> list = repository.listAll();

        for (SpecificationEntity spec : list) {
            List<UnitDefinition> units = spec.units.values()
                    .stream()
                    .map(x -> UnitDefinitionMapper.toDefinition(spec.namespace.code, x))
                    .toList();

            result.addAll(units);
        }

        return result;
    }
}