package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.Window;

public class Ticket2235 extends Application {

    @Override
    public void init() {
        Window w = new Window(getClass().getSimpleName());
        setMainWindow(w);
        // setTheme("tests-tickets");
        createUI((AbstractOrderedLayout) w.getContent());
    }

    private void createUI(AbstractOrderedLayout layout) {
        layout.setSizeFull();

        TextArea tf = new TextArea();
        tf.setCaption("A text field");
        tf.setSizeFull();
        tf.setRows(2);

        layout.addComponent(tf);
    }
}
