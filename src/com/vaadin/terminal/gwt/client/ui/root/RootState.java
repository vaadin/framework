/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.root;

import com.vaadin.terminal.gwt.client.ComponentState;
import com.vaadin.terminal.gwt.client.Connector;

public class RootState extends ComponentState {
    private Connector content;

    public Connector getContent() {
        return content;
    }

    public void setContent(Connector content) {
        this.content = content;
    }

}