package com.vaadin.tests.components.embedded;

import com.vaadin.server.ExternalResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class EmbeddedApplet extends TestBase {

    @Override
    protected String getDescription() {
        return "The sub window should be shown on top of the embedded applet";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8399;
    }

    @Override
    public void setup() {
        final Embedded applet = new Embedded();
        applet.setType(Embedded.TYPE_BROWSER);
        applet.setWidth("400px");
        applet.setHeight("300px");
        applet.setSource(new ExternalResource("/statictestfiles/applet.html"));
        addComponent(applet);

        addComponent(new Button("Remove applet", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                removeComponent(applet);
            }
        }));

        VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        Window window = new Window("Testwindow", layout);
        layout.addComponent(new Label("I am inside the window"));
        applet.getUI().addWindow(window);
    }
}
