package cc.openxiot.product.resource;

import cc.openxiot.product.exception.OxException;

@FunctionalInterface
public interface NamespacePermission {
    void check(String organizationId) throws OxException;
}
