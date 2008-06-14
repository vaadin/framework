package com.itmill.toolkit.tests.tickets;

import java.util.Iterator;
import java.util.LinkedList;

import com.itmill.toolkit.data.Validator;
import com.itmill.toolkit.data.util.MethodProperty;
import com.itmill.toolkit.ui.AbstractField;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.Label;
import com.itmill.toolkit.ui.Select;
import com.itmill.toolkit.ui.Window;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class Ticket1804 extends com.itmill.toolkit.Application {

    LinkedList listOfAllFields = new LinkedList();

    public void init() {

        final Window main = new Window("#1804");
        setMainWindow(main);

        com.itmill.toolkit.ui.Select s;

        s = new Select("Select with null selection allowed; required=true");
        s.setNullSelectionAllowed(true);
        listOfAllFields.add(s);

        s = new Select("Select with null selection NOT allowed; required=true");
        s.setNullSelectionAllowed(false);
        listOfAllFields.add(s);

        s = new Select("Testcase from the ticket #1804");
        s.setWidth(190);
        s.setNullSelectionAllowed(false);
        TestPojo myPojo = new TestPojo();
        s.setPropertyDataSource(new MethodProperty(myPojo, "id"));
        s.addValidator(new EmptyStringValidator(
                "Selection required for test-field"));
        listOfAllFields.add(s);

        for (Iterator i = listOfAllFields.iterator(); i.hasNext();) {
            s = (Select) i.next();
            main.addComponent(s);
            s.addItem("<null>");
            s.addItem("foo");
            s.addItem("");
            s.addItem("bar");
            s.setNullSelectionItemId("<null>");
            s.setImmediate(true);
            s.setRequired(true);
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
                    if (af.isValid()) {
                        msg.append("VALID\n<hr/>");
                    } else {
                        msg.append("INVALID<br/><i>" + af.getErrorMessage()
                                + "</i><hr/>");
                    }
                }
                Window w = new Window("Status of the fields");
                w.setModal(true);
                w.addComponent(new Label(msg.toString(), Label.CONTENT_XHTML));
                main.addWindow(w);
            }
        });
    }

    class TestPojo {
        String id = "";

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

    }

    /** Throws an exception when the string is empty or null. */
    class EmptyStringValidator implements Validator {

        String msg;

        EmptyStringValidator(String msg) {
            this.msg = msg;
        }

        public boolean isValid(Object value) {
            return !(value == null || value.toString().length() == 0);
        }

        public void validate(Object value) throws InvalidValueException {
            if (!isValid(value)) {
                throw new InvalidValueException(msg);
            }
        }

    }

}
