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
import com.vaadin.terminal.gwt.client.LayoutManager;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.ComponentConnector;

public class VPanelPaintable extends AbstractComponentContainerConnector
        implements SimpleManagedLayout, PostLayoutListener {

    public static final String CLICK_EVENT_IDENTIFIER = "click";

    private Integer uidlScrollTop;

    private ClickEventHandler clickEventHandler = new ClickEventHandler(this,
            CLICK_EVENT_IDENTIFIER) {

        @Override
        protected <H extends EventHandler> HandlerRegistration registerHandler(
                H handler, Type<H> type) {
            return getWidget().addDomHandler(handler, type);
        }
    };

    private Integer uidlScrollLeft;

    @Override
    public void init() {
        VPanel panel = getWidget();
        LayoutManager layoutManager = getLayoutManager();

        layoutManager.registerDependency(this, panel.captionNode);
        layoutManager.registerDependency(this, panel.bottomDecoration);
        layoutManager.registerDependency(this, panel.contentNode);
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
            getWidget().contentNode.setClassName(VPanel.CLASSNAME
                    + "-content");
            getWidget().bottomDecoration
                    .setClassName(VPanel.CLASSNAME + "-deco");
            getWidget().captionNode.setClassName(VPanel.CLASSNAME
                    + "-caption");
            boolean hasCaption = false;
            if (getState().getCaption() != null
                    && !"".equals(getState().getCaption())) {
                getWidget().setCaption(getState().getCaption());
                hasCaption = true;
            } else {
                getWidget().setCaption("");
                getWidget().captionNode
                        .setClassName(VPanel.CLASSNAME + "-nocaption");
            }

            // Add proper stylenames for all elements. This way we can prevent
            // unwanted CSS selector inheritance.
            final String captionBaseClass = VPanel.CLASSNAME
                    + (hasCaption ? "-caption" : "-nocaption");
            final String contentBaseClass = VPanel.CLASSNAME + "-content";
            final String decoBaseClass = VPanel.CLASSNAME + "-deco";
            String captionClass = captionBaseClass;
            String contentClass = contentBaseClass;
            String decoClass = decoBaseClass;
            if (getState().hasStyles()) {
                final String[] styles = getState().getStyle().split(" ");
                for (int i = 0; i < styles.length; i++) {
                    captionClass += " " + captionBaseClass + "-" + styles[i];
                    contentClass += " " + contentBaseClass + "-" + styles[i];
                    decoClass += " " + decoBaseClass + "-" + styles[i];
                }
            }
            getWidget().captionNode.setClassName(captionClass);
            getWidget().contentNode.setClassName(contentClass);
            getWidget().bottomDecoration.setClassName(decoClass);
        }
        // Ensure correct implementation
        super.updateFromUIDL(uidl, client);

        if (!isRealUpdate(uidl)) {
            return;
        }

        clickEventHandler.handleEventHandlerRegistration(client);

        getWidget().client = client;
        getWidget().id = uidl.getId();

        getWidget().setIconUri(uidl, client);

        getWidget().handleError(uidl);

        // Render content
        final UIDL layoutUidl = uidl.getChildUIDL(0);
        final ComponentConnector newLayout = client.getPaintable(layoutUidl);
        if (newLayout != getWidget().layout) {
            if (getWidget().layout != null) {
                client.unregisterPaintable(getWidget().layout);
            }
            getWidget()
                    .setWidget(newLayout.getWidget());
            getWidget().layout = newLayout;
        }
        getWidget().layout.updateFromUIDL(layoutUidl, client);

        // We may have actions attached to this panel
        if (uidl.getChildCount() > 1) {
            final int cnt = uidl.getChildCount();
            for (int i = 1; i < cnt; i++) {
                UIDL childUidl = uidl.getChildUIDL(i);
                if (childUidl.getTag().equals("actions")) {
                    if (getWidget().shortcutHandler == null) {
                        getWidget().shortcutHandler = new ShortcutActionHandler(
                                getId(), client);
                    }
                    getWidget().shortcutHandler
                            .updateActionMap(childUidl);
                }
            }
        }

        if (uidl.hasVariable("scrollTop")
                && uidl.getIntVariable("scrollTop") != getWidget().scrollTop) {
            // Sizes are not yet up to date, so changing the scroll position
            // is deferred to after the layout phase
            uidlScrollTop = new Integer(uidl.getIntVariable("scrollTop"));
        }

        if (uidl.hasVariable("scrollLeft")
                && uidl.getIntVariable("scrollLeft") != getWidget().scrollLeft) {
            // Sizes are not yet up to date, so changing the scroll position
            // is deferred to after the layout phase
            uidlScrollLeft = new Integer(uidl.getIntVariable("scrollLeft"));
        }

        // And apply tab index
        if (uidl.hasVariable("tabindex")) {
            getWidget().contentNode.setTabIndex(uidl
                    .getIntVariable("tabindex"));
        }
    }

    public void updateCaption(ComponentConnector component, UIDL uidl) {
        // NOP: layouts caption, errors etc not rendered in Panel
    }

    @Override
    public VPanel getWidget() {
        return (VPanel) super.getWidget();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VPanel.class);
    }

    public void layout() {
        updateSizes();
    }

    void updateSizes() {
        VPanel panel = getWidget();

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

        LayoutManager layoutManager = getLayoutManager();
        int top = layoutManager.getOuterHeight(panel.captionNode);
        int bottom = layoutManager.getOuterHeight(panel.bottomDecoration);

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

    public void postLayout() {
        VPanel panel = getWidget();
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
