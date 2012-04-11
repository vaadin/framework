/*
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.customlayout;

import java.util.HashMap;
import java.util.Map;

import com.google.gwt.core.client.GWT;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.terminal.gwt.client.ComponentConnector;
import com.vaadin.terminal.gwt.client.Connector;
import com.vaadin.terminal.gwt.client.ConnectorHierarchyChangeEvent;
import com.vaadin.terminal.gwt.client.communication.StateChangeEvent;
import com.vaadin.terminal.gwt.client.ui.AbstractLayoutConnector;
import com.vaadin.terminal.gwt.client.ui.Component;
import com.vaadin.terminal.gwt.client.ui.SimpleManagedLayout;
import com.vaadin.ui.CustomLayout;

@Component(CustomLayout.class)
public class CustomLayoutConnector extends AbstractLayoutConnector implements
        SimpleManagedLayout {

    public static class CustomLayoutState extends AbstractLayoutState {
        Map<Connector, String> childLocations = new HashMap<Connector, String>();
        private String templateContents;
        private String templateName;

        public String getTemplateContents() {
            return templateContents;
        }

        public void setTemplateContents(String templateContents) {
            this.templateContents = templateContents;
        }

        public String getTemplateName() {
            return templateName;
        }

        public void setTemplateName(String templateName) {
            this.templateName = templateName;
        }

        public Map<Connector, String> getChildLocations() {
            return childLocations;
        }

        public void setChildLocations(Map<Connector, String> childLocations) {
            this.childLocations = childLocations;
        }

    }

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
