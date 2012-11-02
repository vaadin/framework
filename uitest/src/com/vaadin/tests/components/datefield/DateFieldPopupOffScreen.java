package com.vaadin.tests.components.datefield;

import java.util.Date;
import java.util.Locale;

import com.vaadin.shared.ui.datefield.Resolution;
import com.vaadin.tests.components.AbstractTestCase;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.DateField;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.LegacyWindow;

public class DateFieldPopupOffScreen extends AbstractTestCase {

    @Override
    protected String getDescription() {
        return "Test for the popup position from a DateField. The popup should always be on-screen even if the DateField is close the the edge of the browser.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3639;
    }

    @Override
    public void init() {
        LegacyWindow mainWindow = new LegacyWindow(getClass().getName());

        GridLayout mainLayout = new GridLayout(3, 3);
        mainLayout.setSizeFull();

        DateField df;

        df = createDateField();
        mainLayout.addComponent(df, 2, 0);
        mainLayout.setComponentAlignment(df, Alignment.TOP_RIGHT);

        df = createDateField();
        mainLayout.addComponent(df, 2, 1);
        mainLayout.setComponentAlignment(df, Alignment.MIDDLE_RIGHT);

        df = createDateField();
        mainLayout.addComponent(df, 2, 2);
        mainLayout.setComponentAlignment(df, Alignment.BOTTOM_RIGHT);

        df = createDateField();
        mainLayout.addComponent(df, 0, 2);
        mainLayout.setComponentAlignment(df, Alignment.BOTTOM_LEFT);

        df = createDateField();
        mainLayout.addComponent(df, 1, 2);
        mainLayout.setComponentAlignment(df, Alignment.BOTTOM_CENTER);

        mainWindow.setContent(mainLayout);
        setMainWindow(mainWindow);
    }

    private DateField createDateField() {
        DateField df = new DateField();
        df.setLocale(new Locale("fi"));
        df.setResolution(Resolution.SECOND);
        df.setDescription("This is a long, multiline tooltip.<br/>It should always be on screen so it can be read.");
        df.setValue(new Date(1000000L));
        return df;
    }
}
