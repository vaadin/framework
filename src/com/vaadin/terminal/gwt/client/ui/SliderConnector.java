/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.core.client.Scheduler;
import com.google.gwt.user.client.Command;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.ui.Slider;

@Component(Slider.class)
public class SliderConnector extends AbstractFieldConnector implements
        Paintable {

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        getWidget().client = client;
        getWidget().id = uidl.getId();

        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().immediate = getState().isImmediate();
        getWidget().disabled = !isEnabled();
        getWidget().readonly = isReadOnly();

        getWidget().vertical = uidl.hasAttribute("vertical");

        // TODO should style names be used?

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
