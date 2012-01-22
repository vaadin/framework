/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Document;
import com.google.gwt.dom.client.PreElement;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.KeyDownEvent;
import com.google.gwt.event.dom.client.KeyDownHandler;
import com.google.gwt.event.dom.client.MouseMoveEvent;
import com.google.gwt.event.dom.client.MouseMoveHandler;
import com.google.gwt.event.dom.client.MouseOutEvent;
import com.google.gwt.event.dom.client.MouseOutHandler;
import com.google.gwt.event.dom.client.MouseOverEvent;
import com.google.gwt.event.dom.client.MouseOverHandler;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.Util;
import com.vaadin.terminal.gwt.client.VPaintableWidget;

public class VLabelPaintable implements VPaintableWidget {

    private VLabel widget = GWT.create(VLabel.class);
    private ApplicationConnection client;

    class TooltipHandler implements ClickHandler, KeyDownHandler,
            MouseOverHandler, MouseOutHandler, MouseMoveHandler {

        public void onClick(ClickEvent event) {
            // TODO Auto-generated method stub

        }

        public void onMouseMove(MouseMoveEvent event) {
            // TODO Auto-generated method stub

        }

        public void onMouseOut(MouseOutEvent event) {
            // TODO Auto-generated method stub

        }

        public void onMouseOver(MouseOverEvent event) {
            // TODO Auto-generated method stub

        }

        public void onKeyDown(KeyDownEvent event) {
            // TODO Auto-generated method stub

        }

    }

    public VLabelPaintable() {
        TooltipHandler handler = new TooltipHandler();

        widget.addDomHandler(handler, ClickEvent.getType());
        widget.addDomHandler(handler, KeyDownEvent.getType());
        widget.addDomHandler(handler, MouseOverEvent.getType());
        widget.addDomHandler(handler, MouseOutEvent.getType());
        widget.addDomHandler(handler, MouseMoveEvent.getType());

    }

    public VLabel getWidget() {
        return widget;
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {

        if (client.updateComponent(getWidget(), uidl, true)) {
            return;
        }

        this.client = client;

        boolean sinkOnloads = false;

        final String mode = uidl.getStringAttribute("mode");
        if (mode == null || "text".equals(mode)) {
            getWidget().setText(uidl.getChildString(0));
        } else if ("pre".equals(mode)) {
            PreElement preElement = Document.get().createPreElement();
            preElement.setInnerText(uidl.getChildUIDL(0).getChildString(0));
            // clear existing content
            getWidget().setHTML("");
            // add preformatted text to dom
            getWidget().getElement().appendChild(preElement);
        } else if ("uidl".equals(mode)) {
            getWidget().setHTML(uidl.getChildrenAsXML());
        } else if ("xhtml".equals(mode)) {
            UIDL content = uidl.getChildUIDL(0).getChildUIDL(0);
            if (content.getChildCount() > 0) {
                getWidget().setHTML(content.getChildString(0));
            } else {
                getWidget().setHTML("");
            }
            sinkOnloads = true;
        } else if ("xml".equals(mode)) {
            getWidget().setHTML(uidl.getChildUIDL(0).getChildString(0));
        } else if ("raw".equals(mode)) {
            getWidget().setHTML(uidl.getChildUIDL(0).getChildString(0));
            sinkOnloads = true;
        } else {
            getWidget().setText("");
        }
        if (sinkOnloads) {
            Util.sinkOnloadForImages(getWidget().getElement());
        }
    }

    public Widget getWidgetForPaintable() {
        return getWidget();
    }

}
