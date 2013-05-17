package com.vaadin.tests.components.combobox;

import java.util.Arrays;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.UserError;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;

public class ComboBoxBorder extends TestBase {

    @Override
    protected void setup() {
        setTheme("tests-tickets");

        final ComboBox cb = new ComboBox("All errors", Arrays.asList("Error",
                "Error 2"));
        cb.setStyleName("ComboBoxBorder");
        cb.setImmediate(true);
        cb.setWidth("200px"); // must have with to reproduce

        cb.addListener(new ValueChangeListener() {
            public void valueChange(ValueChangeEvent event) {
                cb.setComponentError(new UserError("Error"));
            }
        });

        addComponent(cb);

    }

    @Override
    protected String getDescription() {
        return "Adding a border as a result of styleName change should not break the ComboBox";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11267;
    }

}
