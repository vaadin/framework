/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.communication;

public class Type {
    private final String baseTypeName;
    private final Type[] parameterTypes;

    public Type(String baseTypeName, Type[] parameterTypes) {
        this.baseTypeName = baseTypeName;
        this.parameterTypes = parameterTypes;
    }

    public String getBaseTypeName() {
        return baseTypeName;
    }

    public Type[] getParameterTypes() {
        return parameterTypes;
    }

    @Override
    public String toString() {
        String string = baseTypeName;
        if (parameterTypes != null) {
            string += '<';
            for (int i = 0; i < parameterTypes.length; i++) {
                if (i != 0) {
                    string += ',';
                }
                string += parameterTypes[i].toString();
            }
            string += '>';
        }

        return string;
    }

}
