/* 
@ITMillApache2LicenseForJavaFiles@
 */

package com.vaadin.tests;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.OrderedLayout;
import com.vaadin.ui.Button.ClickEvent;

public class TestForWindowOpen extends CustomComponent {

    public TestForWindowOpen() {

        final OrderedLayout main = new OrderedLayout();
        setCompositionRoot(main);

        main.addComponent(new Button("Open in this window",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        final ExternalResource r = new ExternalResource(
                                "http://www.google.com");
                        getApplication().getMainWindow().open(r);

                    }

                }));

        main.addComponent(new Button("Open in target \"mytarget\"",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        final ExternalResource r = new ExternalResource(
                                "http://www.google.com");
                        getApplication().getMainWindow().open(r, "mytarget");

                    }

                }));

        main.addComponent(new Button("Open in target \"secondtarget\"",
                new Button.ClickListener() {

                    public void buttonClick(ClickEvent event) {
                        final ExternalResource r = new ExternalResource(
                                "http://www.google.com");
                        getApplication().getMainWindow()
                                .open(r, "secondtarget");

                    }

                }));

    }

}
