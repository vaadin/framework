/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.dom.client.NativeEvent;
import com.google.gwt.user.client.Element;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.MouseEventDetails;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.PaintableMap;

public abstract class LayoutClickEventHandler extends ClickEventHandler {

    public LayoutClickEventHandler(Paintable paintable,
            String clickEventIdentifier) {
        super(paintable, clickEventIdentifier);
    }

    protected abstract Paintable getChildComponent(Element element);

    @Override
    protected void fireClick(NativeEvent event) {
        ApplicationConnection client = getApplicationConnection();
        String pid = PaintableMap.get(getApplicationConnection()).getPid(
                paintable);

        MouseEventDetails mouseDetails = new MouseEventDetails(event,
                getRelativeToElement());
        Paintable childComponent = getChildComponent((Element) event
                .getEventTarget().cast());

        Map<String, Object> parameters = new HashMap<String, Object>();
        parameters.put("mouseDetails", mouseDetails.serialize());
        parameters.put("component", childComponent);

        client.updateVariable(pid, clickEventIdentifier, parameters, true);
    }

}
