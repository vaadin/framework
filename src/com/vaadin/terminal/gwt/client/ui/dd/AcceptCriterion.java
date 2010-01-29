package com.vaadin.terminal.gwt.client.ui.dd;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;

public class AcceptCriterion {
    protected static Map<String, AcceptCriteria> criterion = new HashMap<String, AcceptCriteria>();
    private static AcceptCriterionImpl impl;

    static {
        impl = GWT.create(AcceptCriterionImpl.class);
        impl.populateCriterionMap(criterion);
    }

    public static AcceptCriteria get(String name) {
        return criterion.get(name);
    }

}
