package com.vaadin.tests.components.combobox;

import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.v7.ui.ComboBox;

/**
 * Test UI to verify that focus event actually update the ComboBox suggestion
 * popup
 *
 * @author Vaadin Ltd
 */
public class ComboBoxItemAddingWithFocusListener
        extends AbstractReindeerTestUI {

    private ComboBox cBox;

    @Override
    protected void setup(VaadinRequest request) {
        cBox = new ComboBox();
        addComponent(cBox);
        cBox.setImmediate(true);
        cBox.addItem("Foo");
        cBox.addItem("Bar");
        cBox.addFocusListener(new FocusListener() {

            int x = 0;

            @Override
            public void focus(FocusEvent event) {
                cBox.addItem("Focus" + (x++));
            }

        });
        addComponent(new Button("Focus Target"));
    }

    @Override
    protected String getTestDescription() {
        return "Item adding in focus listener causes popup to clear";
    }

    @Override
    protected Integer getTicketNumber() {
        return 13635;
    }

}
