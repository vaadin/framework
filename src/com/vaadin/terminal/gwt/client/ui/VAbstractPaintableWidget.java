/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.FocusWidget;
import com.google.gwt.user.client.ui.Focusable;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.TooltipInfo;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VPaintableMap;
import com.vaadin.terminal.gwt.client.VPaintableWidget;
import com.vaadin.terminal.gwt.client.VPaintableWidgetContainer;

public abstract class VAbstractPaintableWidget implements VPaintableWidget {

    public static final String ATTRIBUTE_DESCRIPTION = "description";
    public static final String ATTRIBUTE_ERROR = "error";

    private Widget widget;
    private ApplicationConnection connection;
    private String id;

    /* State variables */
    private boolean enabled = true;
    private boolean visible = true;

    /**
     * Default constructor
     */
    public VAbstractPaintableWidget() {
    }

    /**
     * Called after the application connection reference has been set up
     */
    public void init() {
    }

    /**
     * Creates and returns the widget for this VPaintableWidget. This method
     * should only be called once when initializing the paintable.
     * 
     * @return
     */
    protected abstract Widget createWidget();

    /**
     * Returns the widget associated with this paintable. The widget returned by
     * this method must not changed during the life time of the paintable.
     * 
     * @return The widget associated with this paintable
     */
    public Widget getWidgetForPaintable() {
        if (widget == null) {
            widget = createWidget();
        }

        return widget;
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.terminal.gwt.client.VPaintable#getConnection()
     */
    public final ApplicationConnection getConnection() {
        return connection;
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * com.vaadin.terminal.gwt.client.VPaintable#setConnection(com.vaadin.terminal
     * .gwt.client.ApplicationConnection)
     */
    public final void setConnection(ApplicationConnection connection) {
        this.connection = connection;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public VPaintableWidgetContainer getParent() {
        // FIXME: Hierarchy should be set by framework instead of looked up here
        VPaintableMap paintableMap = VPaintableMap.get(getConnection());

        Widget w = getWidgetForPaintable();
        while (true) {
            w = w.getParent();
            if (w == null) {
                return null;
            }
            if (paintableMap.isPaintable(w)) {
                return (VPaintableWidgetContainer) paintableMap.getPaintable(w);
            }
        }
    }

    protected static boolean isRealUpdate(UIDL uidl) {
        return !isCachedUpdate(uidl) && !uidl.getBooleanAttribute("invisible");
    }

    protected static boolean isCachedUpdate(UIDL uidl) {
        return uidl.getBooleanAttribute("cached");
    }

    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        if (isCachedUpdate(uidl)) {
            return;
        }

        VPaintableMap paintableMap = VPaintableMap.get(getConnection());
        // register the listened events by the server-side to the event-handler
        // of the component
        paintableMap.registerEventListenersFromUIDL(getId(), uidl);

        // Visibility
        setVisible(!uidl.getBooleanAttribute("invisible"), uidl);

        if (uidl.getId().startsWith("PID_S")) {
            DOM.setElementProperty(getWidgetForPaintable().getElement(), "id",
                    uidl.getId().substring(5));
        }

        if (!isVisible()) {
            // component is invisible, delete old size to notify parent, if
            // later made visible
            paintableMap.setOffsetSize(this, null);
            return;
        }

        /*
         * Disabled state may affect (override) tabindex so the order must be
         * first setting tabindex, then enabled state.
         */
        if (uidl.hasAttribute("tabindex")
                && getWidgetForPaintable() instanceof Focusable) {
            ((Focusable) getWidgetForPaintable()).setTabIndex(uidl
                    .getIntAttribute("tabindex"));
        }
        setEnabled(!uidl.getBooleanAttribute("disabled"));

        // Style names
        String styleName = getStyleNameFromUIDL(getWidgetForPaintable()
                .getStylePrimaryName(), uidl,
                getWidgetForPaintable() instanceof Field);
        getWidgetForPaintable().setStyleName(styleName);

        // Update tooltip
        TooltipInfo tooltipInfo = paintableMap.getTooltipInfo(this, null);
        if (uidl.hasAttribute(ATTRIBUTE_DESCRIPTION)) {
            tooltipInfo
                    .setTitle(uidl.getStringAttribute(ATTRIBUTE_DESCRIPTION));
        } else {
            tooltipInfo.setTitle(null);
        }
        // add error info to tooltip if present
        if (uidl.hasAttribute(ATTRIBUTE_ERROR)) {
            tooltipInfo.setErrorUidl(uidl.getErrors());
        } else {
            tooltipInfo.setErrorUidl(null);
        }

        // Set captions
        if (delegateCaptionHandling()) {
            getParent().updateCaption(this, uidl);
        }

        /*
         * updateComponentSize need to be after caption update so caption can be
         * taken into account
         */

        getConnection().updateComponentSize(this, uidl);
    }

    /**
     * Sets the enabled state of this paintable
     * 
     * @param enabled
     *            true if the paintable is enabled, false otherwise
     */
    protected void setEnabled(boolean enabled) {
        this.enabled = enabled;

        if (getWidgetForPaintable() instanceof FocusWidget) {
            FocusWidget fw = (FocusWidget) getWidgetForPaintable();
            fw.setEnabled(enabled);
        }

    }

    public boolean isEnabled() {
        return enabled;
    }

    /**
     * Return true if parent handles caption, false if the paintable handles the
     * caption itself.
     * 
     * 
     * @deprecated This should always return true and all components should let
     *             the parent handle the caption and use other attributes for
     *             internal texts in the component
     * @return
     */
    @Deprecated
    protected boolean delegateCaptionHandling() {
        return true;
    }

    /**
     * Sets the visible state for this paintable.
     * 
     * @param visible
     *            true if the paintable should be made visible, false otherwise
     * @param captionUidl
     *            The UIDL that is passed to the parent and onwards to VCaption
     *            if the caption needs to be updated as a result of the
     *            visibility change.
     */
    protected void setVisible(boolean visible, UIDL captionUidl) {
        boolean wasVisible = this.visible;
        this.visible = visible;

        getWidgetForPaintable().setVisible(visible);
        if (wasVisible != visible) {
            // Changed invisibile <-> visible
            if (wasVisible && delegateCaptionHandling()) {
                // Must hide caption when component is hidden
                getParent().updateCaption(this, captionUidl);
            }
        }
    }

    protected boolean isVisible() {
        return visible;
    }

    /**
     * Generates the style name for the widget based on the given primary style
     * name (typically returned by Widget.getPrimaryStyleName()) and the UIDL.
     * An additional "modified" style name can be added if the field parameter
     * is set to true.
     * 
     * @param primaryStyleName
     * @param uidl
     * @param isField
     * @return
     */
    protected static String getStyleNameFromUIDL(String primaryStyleName,
            UIDL uidl, boolean field) {
        boolean enabled = !uidl.getBooleanAttribute("disabled");

        StringBuffer styleBuf = new StringBuffer();
        styleBuf.append(primaryStyleName);

        // first disabling and read-only status
        if (!enabled) {
            styleBuf.append(" ");
            styleBuf.append(ApplicationConnection.DISABLED_CLASSNAME);
        }
        if (uidl.getBooleanAttribute("readonly")) {
            styleBuf.append(" ");
            styleBuf.append("v-readonly");
        }

        // add additional styles as css classes, prefixed with component default
        // stylename
        if (uidl.hasAttribute("style")) {
            final String[] styles = uidl.getStringAttribute("style").split(" ");
            for (int i = 0; i < styles.length; i++) {
                styleBuf.append(" ");
                styleBuf.append(primaryStyleName);
                styleBuf.append("-");
                styleBuf.append(styles[i]);
                styleBuf.append(" ");
                styleBuf.append(styles[i]);
            }
        }

        // add modified classname to Fields
        if (field && uidl.hasAttribute("modified")) {
            styleBuf.append(" ");
            styleBuf.append(ApplicationConnection.MODIFIED_CLASSNAME);
        }

        // add error classname to components w/ error
        if (uidl.hasAttribute(ATTRIBUTE_ERROR)) {
            styleBuf.append(" ");
            styleBuf.append(primaryStyleName);
            styleBuf.append(ApplicationConnection.ERROR_CLASSNAME_EXT);
        }
        // add required style to required components
        if (uidl.hasAttribute("required")) {
            styleBuf.append(" ");
            styleBuf.append(primaryStyleName);
            styleBuf.append(ApplicationConnection.REQUIRED_CLASSNAME_EXT);
        }

        return styleBuf.toString();
    }

}
