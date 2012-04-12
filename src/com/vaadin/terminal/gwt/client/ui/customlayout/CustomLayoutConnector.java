/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.customlayout;


import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangeEvent;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.AbstractLayoutConnector;
import com.vaadin.terminal.gwt.client.ui.Connect;
import com.vaadin.terminal.gwt.client.ui.SimpleManagedLayout;
import com.vaadin.ui.CustomLayout;

@Connect(CustomLayout.class)
public class CustomLayoutConnector extends AbstractLayoutConnector implements
        SimpleManagedLayout {

    @Override
    public CustomLayoutState getState() {
        return (CustomLayoutState) super.getState();
    }

    @Override
    protected void init() {
        super.init();
        getWidget().client = getConnection();
        getWidget().pid = getConnectorId();

    }

    @Override
    public void onStateChanged(StateChangeEvent stateChangeEvent) {
        super.onStateChanged(stateChangeEvent);

        // Evaluate scripts
        VCustomLayout.eval(getWidget().scripts);
        getWidget().scripts = null;

    }

    private void updateHtmlTemplate() {
        if (getWidget().hasTemplate()) {
            // We (currently) only do this once. You can't change the template
            // later on.
            return;
        }
        String templateName = getState().getTemplateName();
        String templateContents = getState().getTemplateContents();

        if (templateName != null) {
            // Get the HTML-template from client. Overrides templateContents
            // (even though both can never be given at the same time)
            templateContents = getConnection().getResource(
                    "layouts/" + templateName + ".html");
            if (templateContents == null) {
                templateContents = "<em>Layout file layouts/"
                        + templateName
                        + ".html is missing. Components will be drawn for debug purposes.</em>";
            }
        }

        getWidget().initializeHTML(templateContents,
                getConnection().getThemeUri());
    }

    @Override
    public void onConnectorHierarchyChange(ConnectorHierarchyChangeEvent event) {
        super.onConnectorHierarchyChange(event);

        // Must do this once here so the HTML has been set up before we start
        // adding child widgets.

        updateHtmlTemplate();

        // For all contained widgets
        for (ComponentConnector child : getChildren()) {
            String location = getState().getChildLocations().get(child);
            try {
                getWidget().setWidget(child.getWidget(), location);
            } catch (final IllegalArgumentException e) {
                // If no location is found, this component is not visible
            }
        }
        for (ComponentConnector oldChild : event.getOldChildren()) {
            if (oldChild.getParent() == this) {
                // Connector still a child of this
                continue;
            }
            Widget oldChildWidget = oldChild.getWidget();
            if (oldChildWidget.isAttached()) {
                // slot of this widget is emptied, remove it
                getWidget().remove(oldChildWidget);
            }
        }

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
