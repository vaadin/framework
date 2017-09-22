package com.vaadin.tests.components.datefield;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.DateField;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class DateFieldWithDefaultValue extends AbstractTestUI {

    static final String DATEFIELD_HAS_DEFAULT = "hasdefault";

    static final String DATEFIELD_REGULAR = "regular";

    @Override
    protected void setup(VaadinRequest request) {
        DateField dfWithDefault = new DateField(
                "Date field with default value 2010-10-01");
        dfWithDefault.setId(DATEFIELD_HAS_DEFAULT);
        LocalDate defaultValue = LocalDate.parse("2010-10-01",
                DateTimeFormatter.ISO_DATE);
        dfWithDefault.setDefaultValue(defaultValue);
        addComponent(dfWithDefault);

        DateField regularDF = new DateField("Regular datefield");
        regularDF.setId(DATEFIELD_REGULAR);
        addComponent(regularDF);

    }

}
