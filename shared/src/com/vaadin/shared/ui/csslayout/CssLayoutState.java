/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.shared.ui.csslayout;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.AbstractLayoutState;

public class CssLayoutState extends AbstractLayoutState {
    private Map<Connector, String> childCss = new HashMap<Connector, String>();

    public Map<Connector, String> getChildCss() {
        return childCss;
    }

    public void setChildCss(Map<Connector, String> childCss) {
        this.childCss = childCss;
    }

}