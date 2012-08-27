/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.metadata;

import com.vaadin.shared.annotations.DelegateToWidget;

public class Property {
    private final Type bean;
    private final String name;

    public Property(Type bean, String name) {
        this.bean = bean;
        this.name = name;
    }

    public Object getValue(Object bean) throws NoDataException {
        return TypeDataStore.getGetter(this).invoke(bean);
    }

    public void setValue(Object bean, Object value) throws NoDataException {
        TypeDataStore.getSetter(this).invoke(bean, value);
    }

    public String getDelegateToWidgetMethodName() {
        String value = TypeDataStore.getDelegateToWidget(this);
        if (value == null) {
            return null;
        } else {
            return DelegateToWidget.Helper.getDelegateTarget(getName(), value);
        }
    }

    public Type getType() throws NoDataException {
        return TypeDataStore.getType(this);
    }

    public String getSignature() {
        return bean.toString() + "." + name;
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

    public String getName() {
        return name;
    }

    @Override
    public String toString() {
        return getSignature();
    }

}
