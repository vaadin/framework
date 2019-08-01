package com.vaadin.tests.components.datefield;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ValueChangeMode;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateTimeField;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.TextField;
import com.vaadin.v7.shared.ui.datefield.Resolution;
import com.vaadin.v7.ui.DateField;

@Widgetset(TestingWidgetSet.NAME)
public class DateTimeFieldEventOrder extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {

        HorizontalLayout horizontalLayout = new HorizontalLayout();

        DateTimeField dateField = new DateTimeField();
        dateField.setResolution(DateTimeResolution.SECOND);
        dateField.setId("test-field");
        dateField.addValueChangeListener(
                event -> log("DateTimeField value change event"));

        Button button = new Button("test");
        button.setId("test-button");
        button.addClickListener(ev -> {
            log("Button Click Event");
        });

        TextField tf = new TextField("test");
        tf.setValueChangeMode(ValueChangeMode.BLUR);
        tf.addValueChangeListener(event -> log("TextField value change event"));

        DateField df = new DateField();
        df.setResolution(Resolution.SECOND);
        df.addValueChangeListener(event -> {
            log("DateTimeField V7 value change event");
        });

        horizontalLayout.addComponents(dateField, button, tf, df);
        addComponent(horizontalLayout);
    }

    @Override
    protected Integer getTicketNumber() {
        return 11316;
    }
}
