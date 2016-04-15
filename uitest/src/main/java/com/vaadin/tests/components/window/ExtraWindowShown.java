package com.vaadin.tests.components.window;

import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

public class ExtraWindowShown extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
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
        getLayout().getParent().setSizeFull();
        getLayout().setSizeFull();
        getLayout().addComponent(b);
        getLayout().setComponentAlignment(b, Alignment.MIDDLE_CENTER);
    }

    @Override
    protected String getTestDescription() {
        return "Sub window shouldn't reappear after closing.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5987;
    }

}
