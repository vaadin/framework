package com.vaadin.tests.tickets;

import java.util.Iterator;
import java.util.LinkedList;

import com.vaadin.data.Validator;
import com.vaadin.data.util.MethodProperty;
import com.vaadin.server.SystemError;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.LegacyWindow;
import com.vaadin.ui.Select;
import com.vaadin.ui.Window;

public class Ticket1804 extends com.vaadin.server.LegacyApplication {

    LinkedList<Select> listOfAllFields = new LinkedList<Select>();

    @Override
    public void init() {

        final LegacyWindow main = new LegacyWindow("#1804");
        setMainWindow(main);

        com.vaadin.ui.Select s;

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
        s.setPropertyDataSource(new MethodProperty<String>(new TestPojo(), "id"));
        s.addValidator(new EmptyStringValidator(
                "Selection required for test-field"));
        s.setRequired(true);
        listOfAllFields.add(s);

        s = new Select("Testcase from the ticket #1804, but without validator");
        s.setNullSelectionAllowed(false);
        s.setPropertyDataSource(new MethodProperty<String>(new TestPojo(), "id"));
        s.setRequired(true);
        listOfAllFields.add(s);

        s = new Select(
                "Testcase from the ticket #1804, but with required=false");
        s.setNullSelectionAllowed(false);
        s.setPropertyDataSource(new MethodProperty<String>(new TestPojo(), "id"));
        s.addValidator(new EmptyStringValidator(
                "Selection required for test-field"));
        listOfAllFields.add(s);

        s = new Select(
                "Testcase from the ticket #1804, but without validator and with required=false");
        s.setNullSelectionAllowed(false);
        s.setPropertyDataSource(new MethodProperty<String>(new TestPojo(), "id"));
        listOfAllFields.add(s);

        s = new Select(
                "Required=true, custom error message, null selection not allowed");
        s.setRequired(true);
        s.setNullSelectionAllowed(false);
        s.setPropertyDataSource(new MethodProperty<String>(new TestPojo(), "id"));
        s.setValue(null);
        s.setComponentError(new SystemError("Test error message"));
        listOfAllFields.add(s);

        for (Iterator<Select> i = listOfAllFields.iterator(); i.hasNext();) {
            s = i.next();
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

            @Override
            public void buttonClick(ClickEvent event) {
                StringBuffer msg = new StringBuffer();
                for (Iterator<Select> i = listOfAllFields.iterator(); i
                        .hasNext();) {
                    AbstractField<?> af = i.next();
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
                w.setHeight("80%");
                w.setContent(new Label(msg.toString(), ContentMode.HTML));
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
    static class EmptyStringValidator implements Validator {

        String msg;

        EmptyStringValidator(String msg) {
            this.msg = msg;
        }

        @Override
        public void validate(Object value) throws InvalidValueException {
            if (value == null || value.toString().length() == 0) {
                throw new InvalidValueException(msg);
            }
        }

    }

}
