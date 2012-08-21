/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.metadata;

public class Property {
    private final Type type;
    private final String name;

    public Property(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public Object getValue(Object bean) throws NoDataException {
        return TypeDataStore.getGetter(this).invoke(bean, null);
    }

    public String getDelegateToWidgetMethod() {
        String value = TypeDataStore.getDelegateToWidget(this);
        if (value == null) {
            return null;
        } else if (value.isEmpty()) {
            return "set" + Character.toUpperCase(value.charAt(0))
                    + value.substring(1);
        } else {
            return value;
        }
    }

    public String getSignature() {
        return type.toString() + "." + name;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        } else if (obj instanceof Property) {
            Property other = (Property) obj;
            return getSignature().equals(other.getSignature());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getSignature().hashCode();
    }

}
