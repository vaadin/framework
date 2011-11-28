package com.vaadin.tests.components.formlayout;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.FormLayout;
import com.vaadin.ui.TextField;

public class FormLayoutReplaceComponent extends TestBase {

    @Override
    protected void setup() {
        addComponent(new FL());

    }

    public class FL extends FormLayout implements ValueChangeListener {

        private TextField messages;
        private CheckBox control;

        @SuppressWarnings("deprecation")
        public FL() {
            setCaption("Test");
            control = new CheckBox("Messages On/Off");
            control.addListener(this);
            control.setImmediate(true);
            addComponent(control);

            // The bug is in replaceComponent, triggered when VTextField is
            // replaced by VTextArea so cannot replace this with TextArea.
            messages = new TextField("Messages");
            messages.setRows(10);
            messages.setColumns(40);
            messages.setVisible(false);
            messages.setEnabled(false);
            addComponent(messages);
        }

        public void valueChange(ValueChangeEvent event) {
            if (event.getProperty() == control) {
                messages.setVisible(control.getValue());
            }

        }
    }

    @Override
    protected String getDescription() {
        return "Check or uncheck the CheckBox to show/hide the messages field inside the FormLayout.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6308;
    }

}