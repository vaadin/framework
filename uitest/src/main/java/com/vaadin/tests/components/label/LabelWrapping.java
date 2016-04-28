package com.vaadin.tests.components.label;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;

public class LabelWrapping extends TestBase {

    @Override
    protected String getDescription() {
        return "A label inside a limited HorizontalLayout should strive to be as wide as possible and only wrap when the size of the layout is reached. The label should look the same if it is rendered initially with the layout or updated later on.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2478;
    }

    @Override
    protected void setup() {
        HorizontalLayout hl = new HorizontalLayout();
        hl.setWidth("250px");

        final String longString = "this is a somewhat long string.";
        final Label longLabel = new Label(longString);

        Button changeLength = new Button("Change length");
        changeLength.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                if (longLabel.getValue().equals(longString)) {
                    longLabel.setValue("");
                } else {
                    longLabel.setValue(longString);
                }
            }
        });

        hl.addComponent(longLabel);
        hl.addComponent(changeLength);

        addComponent(hl);
    }

}
