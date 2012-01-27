/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.VPaintableWidget;

public abstract class VAbstractPaintableWidget implements VPaintableWidget {

    private Widget widget;
    private ApplicationConnection connection;

    /* State variables */
    // private boolean enabled = true;

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

    // public boolean isEnabled() {
    // return enabled;
    // }
}
