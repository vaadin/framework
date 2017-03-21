package com.vaadin.tests.components.uitest.components;

import java.time.LocalDate;

import com.vaadin.shared.ui.datefield.DateResolution;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.tests.components.uitest.TestSampler;
import com.vaadin.ui.AbstractDateField;
import com.vaadin.ui.Component;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.InlineDateField;
import com.vaadin.v7.ui.themes.ChameleonTheme;

public class DatesCssTest extends GridLayout {

    private TestSampler parent;
    private int debugIdCounter = 0;

    private LocalDate date = LocalDate.of(2012, 9, 11);

    public DatesCssTest(TestSampler parent) {
        super(5, 2);
        this.parent = parent;
        setSpacing(true);
        setWidth("100%");

        createDateFieldWith(null, null, null);
        createDateFieldWith("Small", ChameleonTheme.DATEFIELD_SMALL, null);
        createDateFieldWith("Big", ChameleonTheme.DATEFIELD_BIG, null);

        AbstractDateField<LocalDate, DateResolution> df = new DateField("Popup date field");
        df.setId("datefield" + debugIdCounter++);
        df.setValue(date);
        addComponent(df);

        df = new InlineDateField("Inline date field");
        df.setId("datefield" + debugIdCounter++);
        df.setValue(date);
        addComponent(df);

        createDateFieldWith(null, null, "130px");
        createDateFieldWith("Small 130px", ChameleonTheme.DATEFIELD_SMALL,
                "130px");
        createDateFieldWith("Big 130px", ChameleonTheme.DATEFIELD_BIG, "130px");

    }

    private void createDateFieldWith(String caption, String primaryStyleName,
            String width) {
        AbstractDateField<LocalDate, DateResolution> df = new TestDateField("Date field");
        df.setId("datefield" + debugIdCounter++);
        df.setValue(date);

        if (caption != null) {
            df.setCaption(caption);
        }

        if (primaryStyleName != null) {
            df.addStyleName(primaryStyleName);
        }
        if (width != null) {
            df.setWidth(width);
        }

        addComponent(df);

    }

    @Override
    public void addComponent(Component c) {
        parent.registerComponent(c);
        super.addComponent(c);
    }

}
