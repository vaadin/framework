package com.vaadin.tests.tickets;

import com.vaadin.data.Property;
import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TextField;

public class Ticket2053 extends LegacyApplication {

    int childs = 0;

    @Override
    public void init() {

        final LegacyWindow main = new LegacyWindow("#2053");
        setMainWindow(main);
        Button nothing = new Button("Do nothing");
        main.addComponent(nothing);
        nothing.setDescription("Even though no action is taked, this window is refreshed to "
                + "draw changes not originating from this window. Such changes include changes "
                + "made by other browser-windows.");
        Button add = new Button("Add a window", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                final String name = "Child " + (++childs);
                LegacyWindow c = new LegacyWindow(name);

                addWindow(c);
                main.open(new ExternalResource(c.getURL()), "_new");
                main.addComponent(new Label(name + " opened"));
                final TextField tf = new TextField("Non immediate textfield");
                c.addComponent(tf);
                tf.addListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(ValueChangeEvent event) {
                        main.addComponent(new Label(name + " send text:"
                                + tf.getValue()));
                    }
                });
                for (int i = 0; i < 3; i++) {
                    final String caption = "Slow button " + i;
                    c.addComponent(new Button(caption,
                            new Button.ClickListener() {
                                @Override
                                public synchronized void buttonClick(
                                        ClickEvent event) {
                                    try {
                                        this.wait(2000);
                                    } catch (InterruptedException e) {
                                    }
                                    main.addComponent(new Label(caption
                                            + " pressed"));
                                }
                            }));
                }

            }
        });
        main.addComponent(add);
        add.setDescription("This button opens a new browser window. Closing the browser "
                + "window should do two things: 1) submit all unsubmitted state to server "
                + "(print any changes to textfield to main window) and 2) call window.close()"
                + " on the child window (print closed on the main window)");

    }
}
