package com.vaadin.tests.components.combobox;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;

/**
 * Test UI for adding a stylename to a combobox with an undefined width.
 *
 * @author Vaadin Ltd
 */
public class ComboboxStyleChangeWidth extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        final ComboBox<String> cbFoo = new ComboBox<>();
        cbFoo.setItems(
                "A really long string that causes an inline width to be set");
        cbFoo.setSizeUndefined();

        Button btn = new Button("Click to break CB",
                event -> cbFoo.addStyleName("foofoo"));

        addComponent(cbFoo);
        addComponent(btn);

    }

    @Override
    protected String getTestDescription() {
        return "The computed inline width of an undefined-width ComboBox "
                + "(with a sufficiently long option string) breaks when "
                + "the component's stylename is changed after initial "
                + "rendering.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(13444);
    }

}
