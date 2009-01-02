package com.itmill.toolkit.tests.tickets;

import java.util.Iterator;
import java.util.LinkedList;

import com.itmill.toolkit.ui.AbstractField;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket1819 extends com.itmill.toolkit.Application {

    LinkedList listOfAllFields = new LinkedList();

    @Override
    public void init() {

        final Window main = new Window("#1819");
        setMainWindow(main);

        com.itmill.toolkit.ui.Select s;

        s = new Select("Select with null selection allowed");
        s.setNullSelectionAllowed(true);
        listOfAllFields.add(s);

        s = new Select("Select with null selection NOT allowed");
        s.setNullSelectionAllowed(false);
        listOfAllFields.add(s);

        for (Iterator i = listOfAllFields.iterator(); i.hasNext();) {
            s = (Select) i.next();
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

            public void buttonClick(ClickEvent event) {
                StringBuffer msg = new StringBuffer();
                for (java.util.Iterator i = listOfAllFields.iterator(); i
                        .hasNext();) {
                    AbstractField af = (AbstractField) i.next();
                    msg.append("<h1>" + af.getCaption() + "</h1>\n");
                    msg.append("Value=" + af.getValue() + "<br/>\n");
                }
                Window w = new Window("Status of the fields");
                w.setModal(true);
                w.addComponent(new Label(msg.toString(), Label.CONTENT_XHTML));
                main.addWindow(w);
            }
        });
    }

}
