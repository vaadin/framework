package com.vaadin.tests.components.datefield;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.datefield.DateTimeResolution;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.DateTimeField;

/**
 * @author Vaadin Ltd
 *
 */
public class TimePopupSelection extends AbstractTestUIWithLog {

    private static final DateTimeFormatter FORMATTER = DateTimeFormatter
            .ofPattern("dd/MM/yyyy HH:mm:ss");

    @Override
    protected void setup(VaadinRequest request) {
        setLocale(Locale.ENGLISH);
        DateTimeField field = new DateTimeField();
        field.setResolution(DateTimeResolution.SECOND);

        field.setValue(LocalDateTime.of(2017, 1, 13, 1, 0));

        field.addValueChangeListener(
                event -> log(FORMATTER.format(event.getValue())));

        addComponent(field);
    }

}
