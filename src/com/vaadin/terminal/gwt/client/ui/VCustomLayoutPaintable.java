/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.terminal.gwt.client.VPaintableWidget;

public class VCustomLayoutPaintable extends VAbstractPaintableWidgetContainer {

    /** Update the layout from UIDL */
    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidgetForPaintable().client = client;
        // ApplicationConnection manages generic component features
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidgetForPaintable().pid = uidl.getId();
        if (!getWidgetForPaintable().hasTemplate()) {
            // Update HTML template only once
            getWidgetForPaintable().initializeHTML(uidl, client);
        }

        // Evaluate scripts
        VCustomLayout.eval(getWidgetForPaintable().scripts);
        getWidgetForPaintable().scripts = null;

        getWidgetForPaintable().iLayout();
        // TODO Check if this is needed
        client.runDescendentsLayout(getWidgetForPaintable());

        Set<Widget> oldWidgets = new HashSet<Widget>();
        oldWidgets.addAll(getWidgetForPaintable().locationToWidget.values());

        // For all contained widgets
        for (final Iterator<?> i = uidl.getChildIterator(); i.hasNext();) {
            final UIDL uidlForChild = (UIDL) i.next();
            if (uidlForChild.getTag().equals("location")) {
                final String location = uidlForChild.getStringAttribute("name");
                UIDL childUIDL = uidlForChild.getChildUIDL(0);
                final VPaintableWidget childPaintable = client
                        .getPaintable(childUIDL);
                Widget childWidget = childPaintable.getWidgetForPaintable();
                try {
                    getWidgetForPaintable().setWidget(childWidget, location);
                    childPaintable.updateFromUIDL(childUIDL, client);
                } catch (final IllegalArgumentException e) {
                    // If no location is found, this component is not visible
                }
                oldWidgets.remove(childWidget);
            }
        }
        for (Iterator<Widget> iterator = oldWidgets.iterator(); iterator
                .hasNext();) {
            Widget oldWidget = iterator.next();
            if (oldWidget.isAttached()) {
                // slot of this widget is emptied, remove it
                getWidgetForPaintable().remove(oldWidget);
            }
        }

        getWidgetForPaintable().iLayout();
        // TODO Check if this is needed
        client.runDescendentsLayout(getWidgetForPaintable());

    }

    @Override
    public VCustomLayout getWidgetForPaintable() {
        return (VCustomLayout) super.getWidgetForPaintable();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VCustomLayout.class);
    }

    public void updateCaption(VPaintableWidget paintable, UIDL uidl) {
        getWidgetForPaintable().updateCaption(paintable, uidl);

    }
}
