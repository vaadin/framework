package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Root;
import com.vaadin.ui.TextArea;

public class Ticket2235 extends Application.LegacyApplication {

    @Override
    public void init() {
        Root w = new Root(getClass().getSimpleName());
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
