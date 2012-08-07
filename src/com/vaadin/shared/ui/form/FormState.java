/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.shared.ui.form;

import com.vaadin.shared.AbstractFieldState;
import com.vaadin.shared.Connector;

public class FormState extends AbstractFieldState {
    private Connector layout;
    private Connector footer;

    public Connector getLayout() {
        return layout;
    }

    public void setLayout(Connector layout) {
        this.layout = layout;
    }

    public Connector getFooter() {
        return footer;
    }

    public void setFooter(Connector footer) {
        this.footer = footer;
    }

}