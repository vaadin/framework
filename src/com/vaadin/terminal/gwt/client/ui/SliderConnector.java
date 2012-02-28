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

public class SliderConnector extends AbstractComponentConnector {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        getWidget().client = client;
        getWidget().id = uidl.getId();

        // Ensure correct implementation
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().immediate = getState().isImmediate();
        getWidget().disabled = getState().isDisabled();
        getWidget().readonly = getState().isReadOnly();

        getWidget().vertical = uidl.hasAttribute("vertical");

        // TODO should these style names be used?
        String style = getState().getStyle();

        if (getWidget().vertical) {
            getWidget().addStyleName(VSlider.CLASSNAME + "-vertical");
        } else {
            getWidget().removeStyleName(VSlider.CLASSNAME + "-vertical");
        }

        getWidget().min = uidl.getDoubleAttribute("min");
        getWidget().max = uidl.getDoubleAttribute("max");
        getWidget().resolution = uidl.getIntAttribute("resolution");
        getWidget().value = new Double(uidl.getDoubleVariable("value"));

        getWidget().setFeedbackValue(getWidget().value);

        getWidget().buildBase();

        if (!getWidget().vertical) {
            // Draw handle with a delay to allow base to gain maximum width
            Scheduler.get().scheduleDeferred(new Command() {
                public void execute() {
                    getWidget().buildHandle();
                    getWidget().setValue(getWidget().value, false);
                }
            });
        } else {
            getWidget().buildHandle();
            getWidget().setValue(getWidget().value, false);
        }
    }

    @Override
    public VSlider getWidget() {
        return (VSlider) super.getWidget();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VSlider.class);
    }

}
