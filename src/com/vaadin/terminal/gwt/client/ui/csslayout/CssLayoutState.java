/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.csslayout;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.terminal.gwt.client.Connector;
import com.vaadin.terminal.gwt.client.ui.AbstractLayoutState;

public class CssLayoutState extends AbstractLayoutState {
    private Map<Connector, String> childCss = new HashMap<Connector, String>();

    public Map<Connector, String> getChildCss() {
        return childCss;
    }

    public void setChildCss(Map<Connector, String> childCss) {
        this.childCss = childCss;
    }

}