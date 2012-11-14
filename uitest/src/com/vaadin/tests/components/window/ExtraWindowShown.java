package com.vaadin.tests.components.window;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ExtraWindowShown extends TestBase {

    @Override
    protected void setup() {
        Button b = new Button("Open window", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {

                VerticalLayout layout = new VerticalLayout();
                layout.setMargin(true);
                final Window w = new Window("Sub window", layout);
                w.center();
                layout.addComponent(new Button("Close",
                        new Button.ClickListener() {

                            @Override
                            public void buttonClick(ClickEvent event) {
                                w.close();
                            }
                        }));
                Button iconButton = new Button("A button with icon");
                iconButton
                        .setIcon(new ThemeResource("../runo/icons/16/ok.png"));
                layout.addComponent(iconButton);
                event.getButton().getUI().addWindow(w);
            }

        });
        getLayout().setHeight("100%");
        getLayout().addComponent(b);
        getLayout().setComponentAlignment(b, Alignment.MIDDLE_CENTER);
    }

    @Override
    protected String getDescription() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected Integer getTicketNumber() {
        // TODO Auto-generated method stub
        return null;
    }

}
