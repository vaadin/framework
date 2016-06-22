package com.vaadin.tests.components.datefield;

import java.util.Date;
import java.util.Locale;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.DateField;
import com.vaadin.ui.InlineDateField;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.VerticalLayout;

/**
 * Main UI class
 */
@SuppressWarnings("serial")
public class DynamicallyChangeDateRange extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        setLocale(Locale.ENGLISH);
        final VerticalLayout layout = new VerticalLayout();
        layout.setMargin(true);
        setContent(layout);

        final PopupDateField df = new PopupDateField();
        df.setValue(new Date(2012 - 1900, 5 - 1, 12));
        setRange(df, 5);
        layout.addComponent(df);

        final InlineDateField df2 = new InlineDateField();
        df2.setValue(new Date(2012 - 1900, 11 - 1, 16));

        setRange(df2, 5);
        // layout.addComponent(df2);

        Button button1 = new Button("Set Range Now+/-5d");
        button1.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                setRange(df, 5);
                setRange(df2, 5);
            }
        });
        layout.addComponent(button1);

        Button button2 = new Button("Set Range Now+/-10d");
        button2.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                setRange(df, 10);
                setRange(df2, 10);
            }
        });
        layout.addComponent(button2);
    }

    /**
     * @param df
     * @param i
     */
    private void setRange(DateField df, int days) {
        df.setRangeStart(new Date(df.getValue().getTime() - days * 24 * 60 * 60
                * 1000));
        df.setRangeEnd(new Date(df.getValue().getTime() + days * 24 * 60 * 60
                * 1000));

    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTestDescription()
     */
    @Override
    protected String getTestDescription() {
        return "Verifies that the allowed date range can be updated dynamically";
    }

    /*
     * (non-Javadoc)
     * 
     * @see com.vaadin.tests.components.AbstractTestUI#getTicketNumber()
     */
    @Override
    protected Integer getTicketNumber() {
        return 11940;
    }

}
