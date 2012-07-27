/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.components;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.TextField;
import com.vaadin.ui.Window;

public class TooltipInWindow extends AbstractTestRoot {

    @Override
    protected void setup(WrappedRequest request) {
        Window window = new Window("Window");
        window.getContent().setSizeUndefined();
        window.center();
        window.addComponent(createTextField());

        addWindow(window);
        addComponent(createTextField());
    }

    private TextField createTextField() {
        TextField tf = new TextField("TextField with a tooltip");
        tf.setDescription("My tooltip");
        return tf;
    }

    @Override
    protected String getTestDescription() {
        return "Tooltips should also work in a Window (as well as in other overlays)";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(9172);
    }

}
