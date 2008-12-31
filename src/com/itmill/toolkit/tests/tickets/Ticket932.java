package com.itmill.toolkit.tests.tickets;

import com.itmill.toolkit.Application;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.RichTextArea;
import com.itmill.toolkit.ui.TextField;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket932 extends Application {

    @Override
    public void init() {

        final Window mainWin = new Window("Test app for max length feature");
        setMainWindow(mainWin);

        final TextField tx = new TextField(
                "Textfield with maxlenght 10, single row");
        tx.setImmediate(true);
        tx.setMaxLength(10);

        final Label l = new Label();

        Button b = new Button("Check value");
        b.addListener(new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                l.setValue("Length: " + tx.getValue().toString().length()
                        + " Content: " + tx.getValue());
            }
        });

        mainWin.addComponent(tx);
        mainWin.addComponent(b);

        final TextField tx2 = new TextField(
                "Textfield with maxlenght 10, multirow ");
        mainWin.addComponent(tx2);
        tx2.setImmediate(true);
        tx2.setRows(5);
        tx2.setMaxLength(10);

        Button b2 = new Button("Check value");
        b2.addListener(new Button.ClickListener() {

            public void buttonClick(ClickEvent event) {
                l.setValue("Length: " + tx2.getValue().toString().length()
                        + " Content: " + tx2.getValue());
            }
        });

        mainWin.addComponent(tx);
        mainWin.addComponent(b);

        mainWin.addComponent(l);

        final RichTextArea rta = new RichTextArea();
        rta.setCaption("RTA with max lenght 10");

        rta.setMaxLength(10);

        b = new Button("Check value");
        b.addListener(new Button.ClickListener() {
            public void buttonClick(ClickEvent event) {
                l.setValue("Length: " + rta.getValue().toString().length()
                        + " Content: " + rta.getValue());
            }
        });

        mainWin.addComponent(rta);
        mainWin.addComponent(b);

    }

}
