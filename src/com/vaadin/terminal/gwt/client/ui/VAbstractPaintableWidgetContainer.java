/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.VPaintableMap;
import com.vaadin.terminal.gwt.client.VPaintableWidget;
import com.vaadin.terminal.gwt.client.VPaintableWidgetContainer;

public abstract class VAbstractPaintableWidgetContainer extends
        VAbstractPaintableWidget implements VPaintableWidgetContainer {

    /**
     * Default constructor
     */
    public VAbstractPaintableWidgetContainer() {
    }

    public Collection<VPaintableWidget> getChildren() {
        Collection<VPaintableWidget> children = new ArrayList<VPaintableWidget>();

        addDescendantPaintables(getWidgetForPaintable(), children,
                VPaintableMap.get(getConnection()));

        return children;
    }

    private static void addDescendantPaintables(Widget widget,
            Collection<VPaintableWidget> paintables, VPaintableMap paintableMap) {
        // FIXME: Store hierarchy instead of doing lookup every time

        if (widget instanceof HasWidgets) {
            for (Widget child : (HasWidgets) widget) {
                VPaintableWidget paintable = paintableMap.getPaintable(child);
                if (paintable != null) {
                    // If child is a paintable, add it to the collection
                    paintables.add(paintable);
                } else {
                    // Else keep looking for paintables inside the child widget
                    addDescendantPaintables(child, paintables, paintableMap);
                }
            }
        }
    }

}
