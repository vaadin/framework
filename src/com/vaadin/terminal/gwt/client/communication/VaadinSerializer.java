package com.vaadin.terminal.gwt.client.communication;

import com.google.gwt.json.client.JSONObject;
import com.vaadin.terminal.gwt.client.VPaintableMap;

public interface VaadinSerializer {

    // TODO Object -> something
    Object deserialize(JSONObject jsonValue, VPaintableMap idMapper);

}
