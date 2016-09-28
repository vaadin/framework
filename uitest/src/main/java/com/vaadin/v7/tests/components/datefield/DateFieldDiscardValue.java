package com.vaadin.v7.tests.components.datefield;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.v7.data.util.ObjectProperty;
import com.vaadin.v7.ui.DateField;

/**
 * Test to demonstrate how discarding of field value works with various valid
 * and invalid data sources. Previously (Ticket #8069) the case where the
 * content of the datasource was null was not handled correctly. This value is a
 * valid data source value for the field, but discard did not actually discard
 * the value or remove error markers in this cases.
 *
 * @author Vaadin Ltd
 *
 */
public class DateFieldDiscardValue extends AbstractTestUI {

    public static final String PROP_NONULL = "A field with a valid date in the data source property";
    public static final String PROP_NULL_VALUE = "A field with a null value in the data source property";
    public static final String PROP_NULL = "A field with a null datasource property";

    @Override
    protected void setup(VaadinRequest request) {
        String dateFormat = "dd/MM/yy";

        final DateField df = new DateField(PROP_NONULL);
        df.setDateFormat(dateFormat);
        df.setBuffered(true);
        Date date = null;
        try {
            date = new SimpleDateFormat(dateFormat).parse("25/07/16");
        } catch (ParseException e1) {
            // This cannot happen
        }
        ObjectProperty<Date> prop = new ObjectProperty<>(date, Date.class);
        df.setPropertyDataSource(prop);
        Button button = new Button("Discard 1");
        button.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                df.discard();
            }

        });
        VerticalLayout layout = new VerticalLayout();
        HorizontalLayout hLayout = new HorizontalLayout(df, button);
        layout.addComponent(hLayout);

        final DateField df1 = new DateField(PROP_NULL_VALUE);
        df1.setDateFormat(dateFormat);
        df1.setBuffered(true);

        prop = new ObjectProperty<>(null, Date.class);
        df1.setPropertyDataSource(prop);
        button = new Button("Discard 2");
        button.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                df1.discard();
            }

        });
        hLayout = new HorizontalLayout(df1, button);
        layout.addComponent(hLayout);

        final DateField df2 = new DateField(PROP_NULL);
        df2.setDateFormat(dateFormat);
        df2.setBuffered(true);
        df2.setPropertyDataSource(null);
        button = new Button("Discard 3");
        button.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                df2.discard();
            }

        });
        hLayout = new HorizontalLayout(df2, button);
        layout.addComponent(hLayout);

        setContent(layout);

    }

}