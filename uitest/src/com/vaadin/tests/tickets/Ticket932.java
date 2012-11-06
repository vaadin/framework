package com.vaadin.tests.tickets;

import com.vaadin.server.LegacyApplication;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;

public class Ticket932 extends LegacyApplication {

    @Override
    public void init() {

        final LegacyWindow mainWin = new LegacyWindow(
                "Test app for max length feature");
        setMainWindow(mainWin);

        final TextField tx = new TextField(
                "Textfield with maxlenght 10, single row");
        tx.setImmediate(true);
        tx.setMaxLength(10);

        final Label l = new Label();

        Button b = new Button("Check value");
        b.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                l.setValue("Length: " + tx.getValue().toString().length()
                        + " Content: " + tx.getValue());
            }
        });

        mainWin.addComponent(tx);
        mainWin.addComponent(b);

        final TextArea tx2 = new TextArea(
                "Textfield with maxlenght 10, multirow ");
        mainWin.addComponent(tx2);
        tx2.setImmediate(true);
        tx2.setRows(5);
        tx2.setMaxLength(10);

        Button b2 = new Button("Check value");
        b2.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                l.setValue("Length: " + tx2.getValue().toString().length()
                        + " Content: " + tx2.getValue());
            }
        });

        mainWin.addComponent(tx);
        mainWin.addComponent(b);

        mainWin.addComponent(l);

    }

}
