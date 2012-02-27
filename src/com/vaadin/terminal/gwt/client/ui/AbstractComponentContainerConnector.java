/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.ArrayList;
import java.util.Collection;

import com.google.gwt.user.client.ui.HasOneWidget;
import com.google.gwt.user.client.ui.HasWidgets;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ConnectorMap;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ComponentContainerConnector;

public abstract class AbstractComponentContainerConnector extends
        AbstractComponentConnector implements ComponentContainerConnector {

    /**
     * Default constructor
     */
    public AbstractComponentContainerConnector() {
    }

    public Collection<ComponentConnector> getChildren() {
        Collection<ComponentConnector> children = new ArrayList<ComponentConnector>();

        addDescendantPaintables(getWidget(), children,
                ConnectorMap.get(getConnection()));

        return children;
    }

    private static void addDescendantPaintables(Widget widget,
            Collection<ComponentConnector> paintables, ConnectorMap paintableMap) {
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
            Collection<ComponentConnector> paintables, ConnectorMap paintableMap) {
        ComponentConnector paintable = paintableMap.getConnector(widget);
        if (paintable != null) {
            // If widget is a paintable, add it to the collection
            paintables.add(paintable);
        } else {
            // Else keep looking for paintables inside the widget
            addDescendantPaintables(widget, paintables, paintableMap);
        }
    }

}
