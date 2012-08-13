/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.dd;

import com.google.gwt.core.client.GWT;

/**
 * A class via all AcceptCriteria instances are fetched by an identifier.
 */
public class VAcceptCriteria {
    private static VAcceptCriterionFactory impl;

    static {
        impl = GWT.create(VAcceptCriterionFactory.class);
    }

    public static VAcceptCriterion get(String name) {
        return impl.get(name);
    }

}
