/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.ui.HasOneWidget;
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

        addDescendantPaintables(getWidget(), children,
                VPaintableMap.get(getConnection()));

        return children;
    }

    private static void addDescendantPaintables(Widget widget,
            Collection<VPaintableWidget> paintables, VPaintableMap paintableMap) {
        // FIXME: Store hierarchy instead of doing lookup every time

        if (widget instanceof HasWidgets) {
            for (Widget child : (HasWidgets) widget) {
                addIfPaintable(child, paintables, paintableMap);
            }
        } else if (widget instanceof HasOneWidget) {
            Widget child = ((HasOneWidget) widget).getWidget();
            addIfPaintable(child, paintables, paintableMap);
        }
    }

    private static void addIfPaintable(Widget widget,
            Collection<VPaintableWidget> paintables, VPaintableMap paintableMap) {
        VPaintableWidget paintable = paintableMap.getPaintable(widget);
        if (paintable != null) {
            // If widget is a paintable, add it to the collection
            paintables.add(paintable);
        } else {
            // Else keep looking for paintables inside the widget
            addDescendantPaintables(widget, paintables, paintableMap);
        }
    }

}
