/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.client.metadata;

public class TypeData {

    public static Type getType(Class<?> type) {
        return TypeDataStore.getType(type);
    }

    public static Class<?> getClass(String identifier) throws NoDataException {
        return TypeDataStore.getClass(identifier);
    }

    public static boolean hasIdentifier(String identifier) {
        return TypeDataStore.hasIdentifier(identifier);
    }
}
