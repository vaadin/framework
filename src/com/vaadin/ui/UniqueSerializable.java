/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.ui;

import java.io.Serializable;

/**
 * A base class for generating an unique object that is serializable.
 * <p>
 * This class is abstract but has no abstract methods to force users to create
 * an anonymous inner class. Otherwise each instance will not be unique.
 * 
 * @author Vaadin Ltd
 * @version @VERSION@
 * @since 6.8.0
 * 
 */
public abstract class UniqueSerializable implements Serializable {

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return getClass() == obj.getClass();
    }
}
