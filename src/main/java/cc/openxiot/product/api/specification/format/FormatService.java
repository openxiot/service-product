package cc.openxiot.product.api.specification.format;

import cc.openxiot.product.db.specification.SpecificationEntity;
import cc.openxiot.product.db.specification.SpecificationRepository;
import cc.openxiot.product.db.specification.format.FormatDefinitionEntity;
import cc.openxiot.product.db.specification.format.FormatDefinitionMapper;
import cc.openxiot.product.exception.OxException;
import cc.openxiot.product.resource.NamespacePermission;
import cn.geekcity.xiot.spec.definition.FormatDefinition;
import cn.geekcity.xiot.spec.definition.urn.FormatType;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@ApplicationScoped
public class FormatService {

    @Inject
    SpecificationRepository repository;

    public void add(FormatDefinition def, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = repository.findByNamespace(def.type().ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.organization);

        FormatDefinitionEntity found = spec.formats.get(def.type().name());
        if (found != null) {
            throw new IllegalArgumentException("format already exist!");
        }

        FormatDefinitionEntity entity = FormatDefinitionMapper.toEntity(def, null);
        spec.formats.put(entity.code, entity);
        spec.update();
    }

    public void delete(FormatType type, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = repository.findByNamespace(type.ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.organization);

        FormatDefinitionEntity found = spec.formats.get(type.name());
        if (found == null) {
            throw new IllegalArgumentException("format not found!");
        }

        spec.formats.remove(type.name());
        spec.update();
    }

    public void update(FormatDefinition def, NamespacePermission permission) throws OxException {
        SpecificationEntity spec = repository.findByNamespace(def.type().ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found");
        }

        permission.check(spec.organization);

        FormatDefinitionEntity found = spec.formats.get(def.type().name());
        if (found == null) {
            throw new IllegalArgumentException("format not found!");
        }

        FormatDefinitionEntity entity = FormatDefinitionMapper.toEntity(def, null);
        spec.formats.put(entity.code, entity);
        spec.update();
    }

    public FormatDefinition find(FormatType type) {
        SpecificationEntity spec = repository.findByNamespace(type.ns());
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found!");
        }

        FormatDefinitionEntity found = spec.formats.get(type.name());
        if (found == null) {
            throw new IllegalArgumentException("format not found!");
        }

        return FormatDefinitionMapper.toDefinition(spec.namespace.code, found);
    }

    public List<FormatDefinition> findByNamespace(String ns) {
        SpecificationEntity spec = repository.findByNamespace(ns);
        if (spec == null) {
            throw new IllegalArgumentException("namespace not found!");
        }

        return spec.formats.values()
                .stream()
                .map(x -> FormatDefinitionMapper.toDefinition(spec.namespace.code, x))
                .collect(Collectors.toList());
    }

    public List<FormatDefinition> findAll() {
        List<FormatDefinition> result = new ArrayList<>();

        List<SpecificationEntity> list = repository.listAll();

        for (SpecificationEntity spec : list) {
            List<FormatDefinition> formats = spec.formats.values()
                    .stream()
                    .map(x -> FormatDefinitionMapper.toDefinition(spec.namespace.code, x))
                    .toList();

            result.addAll(formats);
        }

        return result;
    }
}