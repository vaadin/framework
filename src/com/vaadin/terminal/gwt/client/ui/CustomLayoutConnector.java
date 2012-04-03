/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui;

import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ApplicationConnection;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.UIDL;
import com.vaadin.ui.CustomLayout;

@Component(CustomLayout.class)
public class CustomLayoutConnector extends AbstractComponentContainerConnector
        implements SimpleManagedLayout {

    /** Update the layout from UIDL */
    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        getWidget().client = client;
        // ApplicationConnection manages generic component features
        super.updateFromUIDL(uidl, client);
        if (!isRealUpdate(uidl)) {
            return;
        }

        getWidget().pid = uidl.getId();
        if (!getWidget().hasTemplate()) {
            // Update HTML template only once
            getWidget().initializeHTML(uidl, client);
        }

        // Evaluate scripts
        VCustomLayout.eval(getWidget().scripts);
        getWidget().scripts = null;

        // TODO Check if this is needed
        client.runDescendentsLayout(getWidget());

        Set<Widget> oldWidgets = new HashSet<Widget>();
        oldWidgets.addAll(getWidget().locationToWidget.values());

        // For all contained widgets
        for (final Iterator<?> i = uidl.getChildIterator(); i.hasNext();) {
            final UIDL uidlForChild = (UIDL) i.next();
            if (uidlForChild.getTag().equals("location")) {
                final String location = uidlForChild.getStringAttribute("name");
                UIDL childUIDL = uidlForChild.getChildUIDL(0);
                final ComponentConnector childPaintable = client
                        .getPaintable(childUIDL);
                Widget childWidget = childPaintable.getWidget();
                try {
                    getWidget().setWidget(childWidget, location);
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
                getWidget().remove(oldWidget);
            }
        }

        // TODO Check if this is needed
        client.runDescendentsLayout(getWidget());

    }

    @Override
    public VCustomLayout getWidget() {
        return (VCustomLayout) super.getWidget();
    }

    @Override
    protected Widget createWidget() {
        return GWT.create(VCustomLayout.class);
    }

    public void updateCaption(ComponentConnector paintable) {
        getWidget().updateCaption(paintable);

    }

    public void layout() {
        getWidget().iLayoutJS(DOM.getFirstChild(getWidget().getElement()));
    }
}
