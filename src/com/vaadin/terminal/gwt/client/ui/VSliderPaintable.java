/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;

public class VSliderPaintable extends VAbstractPaintableWidget {

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        getWidgetForPaintable().client = client;
        getWidgetForPaintable().id = uidl.getId();

        // Ensure correct implementation
        if (client.updateComponent(this, uidl, true)) {
            return;
        }

        getWidgetForPaintable().immediate = uidl
                .getBooleanAttribute("immediate");
        getWidgetForPaintable().disabled = uidl.getBooleanAttribute("disabled");
        getWidgetForPaintable().readonly = uidl.getBooleanAttribute("readonly");

        getWidgetForPaintable().vertical = uidl.hasAttribute("vertical");

        String style = "";
        if (uidl.hasAttribute("style")) {
            style = uidl.getStringAttribute("style");
        }

        if (getWidgetForPaintable().vertical) {
            getWidgetForPaintable().addStyleName(
                    VSlider.CLASSNAME + "-vertical");
        } else {
            getWidgetForPaintable().removeStyleName(
                    VSlider.CLASSNAME + "-vertical");
        }

        getWidgetForPaintable().min = uidl.getDoubleAttribute("min");
        getWidgetForPaintable().max = uidl.getDoubleAttribute("max");
        getWidgetForPaintable().resolution = uidl.getIntAttribute("resolution");
        getWidgetForPaintable().value = new Double(
                uidl.getDoubleVariable("value"));

        getWidgetForPaintable().setFeedbackValue(getWidgetForPaintable().value);

        getWidgetForPaintable().buildBase();

        if (!getWidgetForPaintable().vertical) {
            // Draw handle with a delay to allow base to gain maximum width
            Scheduler.get().scheduleDeferred(new Command() {
                public void execute() {
                    getWidgetForPaintable().buildHandle();
                    getWidgetForPaintable().setValue(
                            getWidgetForPaintable().value, false);
                }
            });
        } else {
            getWidgetForPaintable().buildHandle();
            getWidgetForPaintable().setValue(getWidgetForPaintable().value,
                    false);
        }
    }

    @Override
    public VSlider getWidgetForPaintable() {
        return (VSlider) super.getWidgetForPaintable();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VSlider.class);
    }

}
