/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.EventHelper;
import com.vaadin.terminal.gwt.client.Paintable;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VTooltip;
import com.vaadin.ui.CheckBox;

@Component(CheckBox.class)
public class CheckBoxConnector extends AbstractFieldConnector implements
        Paintable {

    @Override
    protected boolean delegateCaptionHandling() {
        return false;
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Save details
        getWidget().client = client;
        getWidget().id = uidl.getId();

        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().focusHandlerRegistration = EventHelper.updateFocusHandler(
                this, client, getWidget().focusHandlerRegistration);
        getWidget().blurHandlerRegistration = EventHelper.updateBlurHandler(
                this, client, getWidget().blurHandlerRegistration);

        if (null != getState().getErrorMessage()) {
            if (getWidget().errorIndicatorElement == null) {
                getWidget().errorIndicatorElement = DOM.createSpan();
                getWidget().errorIndicatorElement.setInnerHTML("&nbsp;");
                DOM.setElementProperty(getWidget().errorIndicatorElement,
                        "className", "v-errorindicator");
                DOM.appendChild(getWidget().getElement(),
                        getWidget().errorIndicatorElement);
                DOM.sinkEvents(getWidget().errorIndicatorElement,
                        VTooltip.TOOLTIP_EVENTS | Event.ONCLICK);
            } else {
                DOM.setStyleAttribute(getWidget().errorIndicatorElement,
                        "display", "");
            }
        } else if (getWidget().errorIndicatorElement != null) {
            DOM.setStyleAttribute(getWidget().errorIndicatorElement, "display",
                    "none");
        }

        if (isReadOnly()) {
            getWidget().setEnabled(false);
        }

        if (getState().getIcon() != null) {
            if (getWidget().icon == null) {
                getWidget().icon = new Icon(client);
                DOM.insertChild(getWidget().getElement(),
                        getWidget().icon.getElement(), 1);
                getWidget().icon.sinkEvents(VTooltip.TOOLTIP_EVENTS);
                getWidget().icon.sinkEvents(Event.ONCLICK);
            }
            getWidget().icon.setUri(getState().getIcon().getURL());
        } else if (getWidget().icon != null) {
            // detach icon
            DOM.removeChild(getWidget().getElement(),
                    getWidget().icon.getElement());
            getWidget().icon = null;
        }

        // Set text
        getWidget().setText(getState().getCaption());
        getWidget().setValue(
                uidl.getBooleanVariable(getWidget().VARIABLE_STATE));
        getWidget().immediate = getState().isImmediate();
    }

    @Override
    public VCheckBox getWidget() {
        return (VCheckBox) super.getWidget();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VCheckBox.class);
    }

}
