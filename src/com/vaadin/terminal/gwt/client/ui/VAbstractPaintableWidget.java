package com.vaadin.terminal.gwt.client.ui;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.VPaintableWidget;

public abstract class VAbstractPaintableWidget implements VPaintableWidget {

    private Widget widget;

    /**
     * Default constructor
     */
    public VAbstractPaintableWidget() {
    }

    /**
     * Creates and returns the widget for this VPaintableWidget. This method
     * should only be called once when initializing the paintable.
     * 
     * @return
     */
    protected Widget createWidget() {
        return GWT.create(getWidgetClass());
    }

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

    /**
     * Returns the class of the widget for this paintable. Used to instansiate
     * the widget.
     * 
     * @return The widget class.
     */
    protected abstract Class<? extends Widget> getWidgetClass();

}
