package com.vaadin.terminal.gwt.client.communication;

import com.google.gwt.core.client.GWT;
import com.vaadin.terminal.gwt.client.ComponentState_Serializer;
import com.vaadin.terminal.gwt.client.ui.VButtonState_Serializer;

public class SerializerMapImpl implements SerializerMap {

    public VaadinSerializer getSerializer(String type) {
        // TODO This should be in a separate class and constructed by a
        // generator
        if (type.equals("com.vaadin.terminal.gwt.client.ui.VButtonState")) {
            return GWT.create(VButtonState_Serializer.class);
        }

        return GWT.create(ComponentState_Serializer.class);
    }

}
