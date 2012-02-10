/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Style;
import com.google.gwt.dom.client.Style.Unit;
import com.google.gwt.event.dom.client.DomEvent.Type;
import com.google.gwt.event.shared.EventHandler;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.MeasuredSize;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VPaintableWidget;

public class VPanelPaintable extends VAbstractPaintableWidgetContainer
        implements ResizeRequired, LayoutPhaseListener {

    public static final String CLICK_EVENT_IDENTIFIER = "click";

    private Integer uidlScrollTop;

    private ClickEventHandler clickEventHandler = new ClickEventHandler(this,
            CLICK_EVENT_IDENTIFIER) {

        @Override
        protected <H extends EventHandler> HandlerRegistration registerHandler(
                H handler, Type<H> type) {
            return getWidgetForPaintable().addDomHandler(handler, type);
        }
    };

    private Integer uidlScrollLeft;

    public VPanelPaintable() {
        VPanel panel = getWidgetForPaintable();
        MeasuredSize measuredSize = getMeasuredSize();

        measuredSize.registerDependency(panel.captionNode);
        measuredSize.registerDependency(panel.bottomDecoration);
        measuredSize.registerDependency(panel.contentNode);
    }

    @Override
    protected boolean delegateCaptionHandling() {
        return false;
    }

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (isRealUpdate(uidl)) {

            // Handle caption displaying and style names, prior generics.
            // Affects size calculations

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
        super.updateFromUIDL(uidl, client);

        if (!isRealUpdate(uidl)) {
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
            // Sizes are not yet up to date, so changing the scroll position
            // is deferred to after the layout phase
            uidlScrollTop = new Integer(uidl.getIntVariable("scrollTop"));
        }

        if (uidl.hasVariable("scrollLeft")
                && uidl.getIntVariable("scrollLeft") != getWidgetForPaintable().scrollLeft) {
            // Sizes are not yet up to date, so changing the scroll position
            // is deferred to after the layout phase
            uidlScrollLeft = new Integer(uidl.getIntVariable("scrollLeft"));
        }

        // And apply tab index
        if (uidl.hasVariable("tabindex")) {
            getWidgetForPaintable().contentNode.setTabIndex(uidl
                    .getIntVariable("tabindex"));
        }
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

    public void onResize() {
        updateSizes();
    }

    void updateSizes() {
        MeasuredSize measuredSize = getMeasuredSize();
        VPanel panel = getWidgetForPaintable();

        Style contentStyle = panel.contentNode.getStyle();
        if (isUndefinedHeight()) {
            contentStyle.clearHeight();
        } else {
            contentStyle.setHeight(100, Unit.PCT);
        }

        if (isUndefinedWidth()) {
            contentStyle.clearWidth();
        } else {
            contentStyle.setWidth(100, Unit.PCT);
        }

        int top = measuredSize.getDependencyOuterHeight(panel.captionNode);
        int bottom = measuredSize
                .getDependencyOuterHeight(panel.bottomDecoration);

        Style style = panel.getElement().getStyle();
        panel.captionNode.getStyle().setMarginTop(-top, Unit.PX);
        panel.bottomDecoration.getStyle().setMarginBottom(-bottom, Unit.PX);
        style.setPaddingTop(top, Unit.PX);
        style.setPaddingBottom(bottom, Unit.PX);

        // Update scroll positions
        panel.contentNode.setScrollTop(panel.scrollTop);
        panel.contentNode.setScrollLeft(panel.scrollLeft);
        // Read actual value back to ensure update logic is correct
        panel.scrollTop = panel.contentNode.getScrollTop();
        panel.scrollLeft = panel.contentNode.getScrollLeft();
    }

    public void beforeLayout() {
        // Nothing to do
    }

    public void afterLayout() {
        VPanel panel = getWidgetForPaintable();
        if (uidlScrollTop != null) {
            panel.contentNode.setScrollTop(uidlScrollTop.intValue());
            // Read actual value back to ensure update logic is correct
            // TODO Does this trigger reflows?
            panel.scrollTop = panel.contentNode.getScrollTop();
            uidlScrollTop = null;
        }

        if (uidlScrollLeft != null) {
            panel.contentNode.setScrollLeft(uidlScrollLeft.intValue());
            // Read actual value back to ensure update logic is correct
            // TODO Does this trigger reflows?
            panel.scrollLeft = panel.contentNode.getScrollLeft();
            uidlScrollLeft = null;
        }
    }

}
