package com.vaadin.terminal.gwt.client.ui.dd;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;

/**
 * A class via all AcceptCriteria instances are fetched by an identifier.
 */
public class VAcceptCriterion {
    protected static Map<String, VAcceptCriteria> criterion = new HashMap<String, VAcceptCriteria>();
    private static VAcceptCriterionImpl impl;

    static {
        impl = GWT.create(VAcceptCriterionImpl.class);
        impl.populateCriterionMap(criterion);
    }

    public static VAcceptCriteria get(String name) {
        return criterion.get(name);
    }

}
