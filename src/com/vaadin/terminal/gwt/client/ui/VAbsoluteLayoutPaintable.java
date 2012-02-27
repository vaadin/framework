/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.HashSet;
import java.util.Iterator;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.Element;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.DirectionalManagedLayout;
import com.vaadin.terminal.gwt.client.EventId;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VPaintableWidget;
import com.vaadin.terminal.gwt.client.ui.VAbsoluteLayout.AbsoluteWrapper;

public class VAbsoluteLayoutPaintable extends VAbstractPaintableWidgetContainer
        implements DirectionalManagedLayout {

    private LayoutClickEventHandler clickEventHandler = new LayoutClickEventHandler(
            this, EventId.LAYOUT_CLICK) {

        @Override
        protected VPaintableWidget getChildComponent(Element element) {
            return getWidget().getComponent(element);
        }

        @Override
        protected <H extends EventHandler> HandlerRegistration registerHandler(
                H handler, Type<H> type) {
            return getWidget().addDomHandler(handler, type);
        }
    };

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidget().client = client;
        // TODO margin handling
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        clickEventHandler.handleEventHandlerRegistration(client);

        HashSet<String> unrenderedPids = new HashSet<String>(
                getWidget().pidToComponentWrappper.keySet());

        for (Iterator<Object> childIterator = uidl.getChildIterator(); childIterator
                .hasNext();) {
            UIDL cc = (UIDL) childIterator.next();
            if (cc.getTag().equals("cc")) {
                UIDL componentUIDL = cc.getChildUIDL(0);
                unrenderedPids.remove(componentUIDL.getId());
                getWidget().getWrapper(client, componentUIDL)
                        .updateFromUIDL(cc);
            }
        }

        for (String pid : unrenderedPids) {
            AbsoluteWrapper absoluteWrapper = getWidget().pidToComponentWrappper
                    .get(pid);
            getWidget().pidToComponentWrappper.remove(pid);
            absoluteWrapper.destroy();
        }
    }

    public void updateCaption(VPaintableWidget component, UIDL uidl) {
        AbsoluteWrapper parent2 = (AbsoluteWrapper) (component
                .getWidget()).getParent();
        parent2.updateCaption(uidl);
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VAbsoluteLayout.class);
    }

    @Override
    public VAbsoluteLayout getWidget() {
        return (VAbsoluteLayout) super.getWidget();
    }

    public void layoutVertically() {
        VAbsoluteLayout layout = getWidget();
        for (VPaintableWidget paintable : getChildren()) {
            Widget widget = paintable.getWidget();
            AbsoluteWrapper wrapper = (AbsoluteWrapper) widget.getParent();
            Style wrapperStyle = wrapper.getElement().getStyle();

            if (paintable.isRelativeHeight()) {
                int h;
                if (wrapper.top != null && wrapper.bottom != null) {
                    h = wrapper.getOffsetHeight();
                } else if (wrapper.bottom != null) {
                    // top not defined, available space 0... bottom of
                    // wrapper
                    h = wrapper.getElement().getOffsetTop()
                            + wrapper.getOffsetHeight();
                } else {
                    // top defined or both undefined, available space ==
                    // canvas - top
                    h = layout.canvas.getOffsetHeight()
                            - wrapper.getElement().getOffsetTop();
                }
                wrapperStyle.setHeight(h, Unit.PX);
            } else {
                wrapperStyle.clearHeight();
            }

            wrapper.updateCaptionPosition();
        }
    }

    public void layoutHorizontally() {
        VAbsoluteLayout layout = getWidget();
        for (VPaintableWidget paintable : getChildren()) {
            Widget widget = paintable.getWidget();
            AbsoluteWrapper wrapper = (AbsoluteWrapper) widget.getParent();
            Style wrapperStyle = wrapper.getElement().getStyle();

            if (paintable.isRelativeWidth()) {
                int w;
                if (wrapper.left != null && wrapper.right != null) {
                    w = wrapper.getOffsetWidth();
                } else if (wrapper.right != null) {
                    // left == null
                    // available width == right edge == offsetleft + width
                    w = wrapper.getOffsetWidth()
                            + wrapper.getElement().getOffsetLeft();
                } else {
                    // left != null && right == null || left == null &&
                    // right == null
                    // available width == canvas width - offset left
                    w = layout.canvas.getOffsetWidth()
                            - wrapper.getElement().getOffsetLeft();
                }
                wrapperStyle.setWidth(w, Unit.PX);
            } else {
                wrapperStyle.clearWidth();
            }

            wrapper.updateCaptionPosition();
        }
    }
}
