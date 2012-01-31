package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VPaintableWidget;

public class VPanelPaintable extends VAbstractPaintableWidgetContainer {

    public static final String CLICK_EVENT_IDENTIFIER = "click";

    private ClickEventHandler clickEventHandler = new ClickEventHandler(this,
            CLICK_EVENT_IDENTIFIER) {

        @Override
        protected <H extends EventHandler> HandlerRegistration registerHandler(
                H handler, Type<H> type) {
            return getWidgetForPaintable().addDomHandler(handler, type);
        }
    };

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidgetForPaintable().rendering = true;
        if (!uidl.hasAttribute("cached")) {

            // Handle caption displaying and style names, prior generics.
            // Affects size
            // calculations

            // Restore default stylenames
            getWidgetForPaintable().contentNode.setClassName(VPanel.CLASSNAME
                    + "-content");
            getWidgetForPaintable().bottomDecoration
                    .setClassName(VPanel.CLASSNAME + "-deco");
            getWidgetForPaintable().captionNode.setClassName(VPanel.CLASSNAME
                    + "-caption");
            boolean hasCaption = false;
            if (uidl.hasAttribute("caption")
                    && !uidl.getStringAttribute("caption").equals("")) {
                getWidgetForPaintable().setCaption(
                        uidl.getStringAttribute("caption"));
                hasCaption = true;
            } else {
                getWidgetForPaintable().setCaption("");
                getWidgetForPaintable().captionNode
                        .setClassName(VPanel.CLASSNAME + "-nocaption");
            }

            // Add proper stylenames for all elements. This way we can prevent
            // unwanted CSS selector inheritance.
            if (uidl.hasAttribute("style")) {
                final String[] styles = uidl.getStringAttribute("style").split(
                        " ");
                final String captionBaseClass = VPanel.CLASSNAME
                        + (hasCaption ? "-caption" : "-nocaption");
                final String contentBaseClass = VPanel.CLASSNAME + "-content";
                final String decoBaseClass = VPanel.CLASSNAME + "-deco";
                String captionClass = captionBaseClass;
                String contentClass = contentBaseClass;
                String decoClass = decoBaseClass;
                for (int i = 0; i < styles.length; i++) {
                    captionClass += " " + captionBaseClass + "-" + styles[i];
                    contentClass += " " + contentBaseClass + "-" + styles[i];
                    decoClass += " " + decoBaseClass + "-" + styles[i];
                }
                getWidgetForPaintable().captionNode.setClassName(captionClass);
                getWidgetForPaintable().contentNode.setClassName(contentClass);
                getWidgetForPaintable().bottomDecoration
                        .setClassName(decoClass);

            }
        }
        // Ensure correct implementation
        if (client.updateComponent(this, uidl, false)) {
            getWidgetForPaintable().rendering = false;
            return;
        }

        clickEventHandler.handleEventHandlerRegistration(client);

        getWidgetForPaintable().client = client;
        getWidgetForPaintable().id = uidl.getId();

        getWidgetForPaintable().setIconUri(uidl, client);

        getWidgetForPaintable().handleError(uidl);

        // Render content
        final UIDL layoutUidl = uidl.getChildUIDL(0);
        final VPaintableWidget newLayout = client.getPaintable(layoutUidl);
        if (newLayout != getWidgetForPaintable().layout) {
            if (getWidgetForPaintable().layout != null) {
                client.unregisterPaintable(getWidgetForPaintable().layout);
            }
            getWidgetForPaintable()
                    .setWidget(newLayout.getWidgetForPaintable());
            getWidgetForPaintable().layout = newLayout;
        }
        getWidgetForPaintable().layout.updateFromUIDL(layoutUidl, client);

        // We may have actions attached to this panel
        if (uidl.getChildCount() > 1) {
            final int cnt = uidl.getChildCount();
            for (int i = 1; i < cnt; i++) {
                UIDL childUidl = uidl.getChildUIDL(i);
                if (childUidl.getTag().equals("actions")) {
                    if (getWidgetForPaintable().shortcutHandler == null) {
                        getWidgetForPaintable().shortcutHandler = new ShortcutActionHandler(
                                getId(), client);
                    }
                    getWidgetForPaintable().shortcutHandler
                            .updateActionMap(childUidl);
                }
            }
        }

        if (uidl.hasVariable("scrollTop")
                && uidl.getIntVariable("scrollTop") != getWidgetForPaintable().scrollTop) {
            getWidgetForPaintable().scrollTop = uidl
                    .getIntVariable("scrollTop");
            getWidgetForPaintable().contentNode
                    .setScrollTop(getWidgetForPaintable().scrollTop);
            // re-read the actual scrollTop in case invalid value was set
            // (scrollTop != 0 when no scrollbar exists, other values would be
            // caught by scroll listener), see #3784
            getWidgetForPaintable().scrollTop = getWidgetForPaintable().contentNode
                    .getScrollTop();
        }

        if (uidl.hasVariable("scrollLeft")
                && uidl.getIntVariable("scrollLeft") != getWidgetForPaintable().scrollLeft) {
            getWidgetForPaintable().scrollLeft = uidl
                    .getIntVariable("scrollLeft");
            getWidgetForPaintable().contentNode
                    .setScrollLeft(getWidgetForPaintable().scrollLeft);
            // re-read the actual scrollTop in case invalid value was set
            // (scrollTop != 0 when no scrollbar exists, other values would be
            // caught by scroll listener), see #3784
            getWidgetForPaintable().scrollLeft = getWidgetForPaintable().contentNode
                    .getScrollLeft();
        }

        // Must be run after scrollTop is set as Webkit overflow fix re-sets the
        // scrollTop
        getWidgetForPaintable().runHacks(false);

        // And apply tab index
        if (uidl.hasVariable("tabindex")) {
            getWidgetForPaintable().contentNode.setTabIndex(uidl
                    .getIntVariable("tabindex"));
        }

        getWidgetForPaintable().rendering = false;

    }

    public void updateCaption(VPaintableWidget component, UIDL uidl) {
        // NOP: layouts caption, errors etc not rendered in Panel
    }

    @Override
    public VPanel getWidgetForPaintable() {
        return (VPanel) super.getWidgetForPaintable();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VPanel.class);
    }

}
