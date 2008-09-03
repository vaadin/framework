package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.data.Property;
import com.itmill.toolkit.data.Property.ValueChangeEvent;
import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;
import com.itmill.toolkit.ui.Window.CloseEvent;

public class Ticket2053 extends Application {

    int childs = 0;

    @Override
    public void init() {

        final Window main = new Window("#2053");
        setMainWindow(main);
        Button nothing = new Button("Do nothing");
        main.addComponent(nothing);
        nothing
                .setDescription("Even though no action is taked, this window is refreshed to "
                        + "draw changes not originating from this window. Such changes include changes "
                        + "made by other browser-windows.");
        Button add = new Button("Add a window", new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                final String name = "Child " + (++childs);
                Window c = new Window(name);
                c.addListener(new Window.CloseListener() {
                    public void windowClose(CloseEvent e) {
                        main.addComponent(new Label(name + " closed"));
                    }
                });
                addWindow(c);
                main.open(new ExternalResource(c.getURL()), "_new");
                main.addComponent(new Label(name + " opened"));
                final TextField tf = new TextField("Non immediate textfield");
                c.addComponent(tf);
                tf.addListener(new Property.ValueChangeListener() {
                    public void valueChange(ValueChangeEvent event) {
                        main.addComponent(new Label(name + " send text:"
                                + tf.toString()));
                    }
                });
            }
        });
        main.addComponent(add);
        add
                .setDescription("This button opens a new browser window. Closing the browser "
                        + "window should do two things: 1) submit all unsubmitted state to server "
                        + "(print any changes to textfield to main window) and 2) call window.close()"
                        + " on the child window (print closed on the main window)");

    }
}
