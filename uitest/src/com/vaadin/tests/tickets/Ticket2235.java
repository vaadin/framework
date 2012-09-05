package com.vaadin.tests.tickets;

import com.vaadin.Application;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.UI.LegacyWindow;

public class Ticket2235 extends Application {

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
