/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.shared.communication;

import java.io.Serializable;

public class UidlValue implements Serializable {
    private Object value;

    public UidlValue(Object value) {
        this.value = value;
    }

    public Object getValue() {
        return value;
    }

    @Override
    public String toString() {
        return "" + value;
    }

}
