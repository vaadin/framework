/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.terminal.gwt.client.metadata;

public class Method {

    private final Type type;
    private final String name;

    public Method(Type type, String name) {
        this.type = type;
        this.name = name;
    }

    public Type getType() {
        return type;
    }

    public String getName() {
        return name;
    }

    public Type getReturnType() {
        return TypeDataStore.getReturnType(this);
    }

    public void invoke(Object target, Object... params) {
        TypeDataStore.getInvoker(this).invoke(target, params);
    }

    public String getSignature() {
        return type.toString() + "." + name;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) {
            return true;
        } else if (obj instanceof Method) {
            Method other = (Method) obj;
            return other.getSignature().equals(getSignature());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return getSignature().hashCode();
    }

}
