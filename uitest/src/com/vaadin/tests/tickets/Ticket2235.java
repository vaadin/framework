package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TextArea;

public class Ticket2235 extends LegacyApplication {

    @Override
    public void init() {
        LegacyWindow w = new LegacyWindow(getClass().getSimpleName());
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
