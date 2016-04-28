package com.vaadin.tests.components.datefield;

import java.util.Date;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.DateField;

public class WidthRecalculationOnEnableStateChange extends TestBase {
    @Override
    public void setup() {
        setTheme("reindeer-tests");

        final DateField df = new DateField();
        df.setValue(new Date(1203910239L));
        df.setResolution(DateField.RESOLUTION_SEC);
        df.setWidth("200px");
        df.addStyleName("enabled-readonly-styled");
        addComponent(df);
        addComponent(new Button("Toggle disabled for date field",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        df.setEnabled(!df.isEnabled());
                    }
                }));
        addComponent(new Button("Toggle read only for date field",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        df.setReadOnly(!df.isReadOnly());
                    }
                }));
    }

    @Override
    protected String getDescription() {
        return "Setting the disabled state doesn't recalculate the input element width. Setting the read-only state instead recalculates the width. In both cases, the popup button is hidden using CSS.<br><br>The DateField is also given a style name 'test', but that style isn't applied on the calendar popup element.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 8085;
    }

}
