package com.vaadin.tests.tickets;

import java.util.Iterator;
import java.util.LinkedList;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Select;
import com.vaadin.ui.Window;

public class Ticket1819 extends com.vaadin.server.LegacyApplication {

    LinkedList<Select> listOfAllFields = new LinkedList<Select>();

    @Override
    public void init() {

        final LegacyWindow main = new LegacyWindow("#1819");
        setMainWindow(main);

        com.vaadin.ui.Select s;

        s = new Select("Select with null selection allowed");
        s.setNullSelectionAllowed(true);
        listOfAllFields.add(s);

        s = new Select("Select with null selection NOT allowed");
        s.setNullSelectionAllowed(false);
        listOfAllFields.add(s);

        for (Iterator<Select> i = listOfAllFields.iterator(); i.hasNext();) {
            s = i.next();
            main.addComponent(s);
            s.addItem("-null-");
            s.addItem("");
            s.addItem("foo");
            s.addItem("bar");
            s.setNullSelectionItemId("-null-");
            s.setImmediate(true);
        }

        Button checkValidity = new Button("Check validity of the fields");
        main.addComponent(checkValidity);
        checkValidity.addListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                StringBuffer msg = new StringBuffer();
                for (Iterator<Select> i = listOfAllFields.iterator(); i
                        .hasNext();) {
                    AbstractField<?> af = i.next();
                    msg.append("<h1>" + af.getCaption() + "</h1>\n");
                    msg.append("Value=" + af.getValue() + "<br/>\n");
                }
                Window w = new Window("Status of the fields");
                w.setModal(true);
                w.setContent(new Label(msg.toString(), ContentMode.HTML));
                main.addWindow(w);
            }
        });
    }

}
