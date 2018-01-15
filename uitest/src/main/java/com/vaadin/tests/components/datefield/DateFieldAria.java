package com.vaadin.tests.components.datefield;

import java.time.LocalDate;
import java.util.Arrays;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.AbstractDateFieldState.AccessibleElement;
import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.DateField;
import com.vaadin.ui.InlineDateField;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class DateFieldAria extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        DateField dateField = new DateField("Accessible DateField",
                LocalDate.now());
        addComponent(dateField);

        InlineDateField inlineDateField = new InlineDateField(
                "Accessible InlineDateField", LocalDate.now());
        addComponent(inlineDateField);

        ComboBox<DateResolution> resolutions = new ComboBox<>("Date resolution",
                Arrays.asList(DateResolution.values()));
        resolutions.setValue(DateResolution.DAY);
        resolutions.addValueChangeListener(e -> {
            dateField.setResolution(e.getValue());
            inlineDateField.setResolution(e.getValue());
        });
        addComponent(resolutions);

        addComponent(new Button("Change assistive labels", e -> {
            dateField.setAssistiveLabel(AccessibleElement.PREVIOUS_MONTH,
                    "Navigate to previous month");
            inlineDateField.setAssistiveLabel(AccessibleElement.NEXT_MONTH,
                    "Navigate to next month");
        }));
    }
}
