/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.customlayout;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.terminal.gwt.client.Connector;
import com.vaadin.terminal.gwt.client.ui.AbstractLayoutState;

public class CustomLayoutState extends AbstractLayoutState {
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