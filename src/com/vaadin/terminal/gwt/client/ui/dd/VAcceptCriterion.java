package com.vaadin.terminal.gwt.client.ui.dd;

import com.google.gwt.core.client.GWT;

/**
 * A class via all AcceptCriteria instances are fetched by an identifier.
 */
public class VAcceptCriterion {
    private static VAcceptCriterionImpl impl;

    static {
        impl = GWT.create(VAcceptCriterionImpl.class);
        impl.init();
    }

    public static VAcceptCriteria get(String name) {
        return impl.get(name);
    }

}
