/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.shared.ui.root;

import com.vaadin.shared.ComponentState;
import com.vaadin.shared.Connector;

public class RootState extends ComponentState {
    private Connector content;

    public Connector getContent() {
        return content;
    }

    public void setContent(Connector content) {
        this.content = content;
    }

}