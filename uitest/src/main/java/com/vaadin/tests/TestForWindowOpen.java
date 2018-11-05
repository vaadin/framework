package com.vaadin.tests;

import com.vaadin.ui.Button;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

public class TestForWindowOpen extends CustomComponent {

    public TestForWindowOpen() {

        final VerticalLayout main = new VerticalLayout();
        setCompositionRoot(main);

        main.addComponent(new Button("Open in this window", event -> UI
                .getCurrent().getPage().setLocation("http://www.google.com")));

        main.addComponent(new Button("Open in target \"mytarget\"",
                event -> UI.getCurrent().getPage().open("http://www.google.com",
                        "mytarget")));

        main.addComponent(new Button("Open in target \"secondtarget\"",
                event -> UI.getCurrent().getPage().open("http://www.google.com",
                        "secondtarget")));
    }

}
