/* 
@VaadinApache2LicenseForJavaFiles@
 */

package com.vaadin.tests.minitutorials.v7a3;

import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Root;

public class FlotJavaScriptRoot extends Root {

    @Override
    protected void init(WrappedRequest request) {
        final Flot flot = new Flot();
        flot.setHeight("300px");
        flot.setWidth("400px");

        flot.addSeries(1, 2, 4, 8, 16);
        addComponent(flot);

        addComponent(new Button("Highlight point", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                flot.highlight(0, 3);
            }
        }));
    }

}
