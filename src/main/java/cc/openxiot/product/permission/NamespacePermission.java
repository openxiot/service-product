package cc.openxiot.product.permission;

import cc.openxiot.common.exception.OxException;

@FunctionalInterface
public interface NamespacePermission {
    void check(String organizationId) throws OxException;
}
