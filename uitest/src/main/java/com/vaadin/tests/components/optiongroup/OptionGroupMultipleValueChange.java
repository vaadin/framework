package com.vaadin.tests.components.optiongroup;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Label;
import com.vaadin.v7.ui.OptionGroup;

public class OptionGroupMultipleValueChange extends TestBase {

    @Override
    protected String getDescription() {
        return "Clicking on the description of an option should behave exactly like clicking on the radio button. No extra 'null' valuechange event should be sent";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3066;
    }

    @Override
    protected void setup() {
        final OptionGroup og = new OptionGroup();
        og.addItem(
                "Clicking on the text might cause an extra valuechange event");
        og.addItem("Second option, same thing");
        og.setImmediate(true);
        addComponent(og);

        final Label events = new Label("", ContentMode.PREFORMATTED);
        events.setWidth(null);
        addComponent(events);

        og.addValueChangeListener(event -> {
            String s = "ValueChange: " + event.getProperty().getValue();
            events.setValue(events.getValue() + "\n" + s);
        });
    }
}
