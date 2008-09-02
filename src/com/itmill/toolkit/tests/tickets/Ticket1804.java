package com.itmill.toolkit.tests.tickets;

import java.util.Iterator;
import java.util.LinkedList;

import com.itmill.toolkit.data.Validator;
import com.itmill.toolkit.data.util.MethodProperty;
import com.itmill.toolkit.terminal.Sizeable;
import com.itmill.toolkit.terminal.SystemError;
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
        s.setRequired(true);
        listOfAllFields.add(s);

        s = new Select("Select with null selection NOT allowed; required=true");
        s.setNullSelectionAllowed(false);
        s.setRequired(true);
        listOfAllFields.add(s);

        s = new Select("Testcase from the ticket #1804");
        s.setNullSelectionAllowed(false);
        s.setPropertyDataSource(new MethodProperty(new TestPojo(), "id"));
        s.addValidator(new EmptyStringValidator(
                "Selection required for test-field"));
        s.setRequired(true);
        listOfAllFields.add(s);

        s = new Select("Testcase from the ticket #1804, but without validator");
        s.setNullSelectionAllowed(false);
        s.setPropertyDataSource(new MethodProperty(new TestPojo(), "id"));
        s.setRequired(true);
        listOfAllFields.add(s);

        s = new Select(
                "Testcase from the ticket #1804, but with required=false");
        s.setNullSelectionAllowed(false);
        s.setPropertyDataSource(new MethodProperty(new TestPojo(), "id"));
        s.addValidator(new EmptyStringValidator(
                "Selection required for test-field"));
        listOfAllFields.add(s);

        s = new Select(
                "Testcase from the ticket #1804, but without validator and with required=false");
        s.setNullSelectionAllowed(false);
        s.setPropertyDataSource(new MethodProperty(new TestPojo(), "id"));
        listOfAllFields.add(s);

        s = new Select(
                "Required=true, custom error message, null selection not allowed");
        s.setRequired(true);
        s.setNullSelectionAllowed(false);
        s.setPropertyDataSource(new MethodProperty(new TestPojo(), "id"));
        s.setValue(null);
        s.setComponentError(new SystemError("Test error message"));
        listOfAllFields.add(s);

        for (Iterator i = listOfAllFields.iterator(); i.hasNext();) {
            s = (Select) i.next();
            main.addComponent(s);
            s.addItem("foo");
            s.addItem("");
            s.addItem("bar");
            if (s.isNullSelectionAllowed()) {
                s.addItem("<null>");
                s.setNullSelectionItemId("<null>");
            }
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
                    if (af.isValid()) {
                        msg.append("VALID\n<hr/>");
                    } else {
                        msg.append("INVALID<br/><i>" + af.getErrorMessage()
                                + "</i><hr/>");
                    }
                }
                Window w = new Window("Status of the fields");
                w.setModal(true);
                w.setScrollable(true);
                w.setHeight(80);
                w.setHeightUnits(Sizeable.UNITS_PERCENTAGE);
                w.addComponent(new Label(msg.toString(), Label.CONTENT_XHTML));
                main.addWindow(w);
            }
        });
    }

    public class TestPojo {
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
