/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.metadata;

public class TypeData {

    public static Type getType(Class<?> type) {
        return TypeDataStore.getType(type);
    }

    public static Type getType(String identifier) {
        return TypeDataStore.getType(getClass(identifier));
    }

    public static Class<?> getClass(String identifier) {
        return TypeDataStore.getClass(identifier);
    }
}
