package com.vaadin.tests.components.datefield;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;

@SuppressWarnings("serial")
public class DateFieldEmptyValid extends TestBase {

    private Log log;

    private MyDateField df;

    private SimpleDateFormat formatter = new SimpleDateFormat(
            "MMMM d, yyyy hh:mm:ss aaa", Locale.US);

    public class MyDateField extends PopupDateField {
        @Override
        public boolean isEmpty() {
            return super.isEmpty();
        }

    }

    @Override
    protected void setup() {
        addComponent(new Label("<br/><br/>", ContentMode.HTML));
        log = new Log(8);
        addComponent(log);
        df = new MyDateField();
        df.setId("DateField");
        df.setRequired(true);
        df.setLocale(new Locale("fi", "FI"));
        df.setValue(new Date(100000000000L));
        df.setImmediate(true);
        df.setResolution(DateField.RESOLUTION_DAY);
        df.addListener(new ValueChangeListener() {
            @Override
            public void valueChange(ValueChangeEvent event) {
                log.log("Value changeEvent");
                checkEmpty();
            }
        });
        addComponent(df);
        checkEmpty();
        Button b = new Button("Clear date");
        b.setId("clear");
        b.addListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                log.log("Clearing date aka setValue(null)");
                df.setValue(null);
            }
        });
        addComponent(b);

        b = new Button("Set date to 4.5.1990");
        b.setId("set4.5.1990");
        b.addListener(new ClickListener() {

            @Override
            @SuppressWarnings("deprecation")
            public void buttonClick(ClickEvent event) {
                log.log("Setting new value to datefield (4.5.1990)");
                df.setValue(new Date(1990 - 1900, 5 - 1, 4));
            }
        });
        addComponent(b);

        b = new Button("Set date to 5.6.2000 using a property data source");
        b.addListener(new ClickListener() {

            @Override
            @SuppressWarnings("deprecation")
            public void buttonClick(ClickEvent event) {
                log.log("Setting new object property (5.6.2000) to datefield");
                ObjectProperty<Date> dfProp = new ObjectProperty<Date>(
                        new Date(2000 - 1900, 6 - 1, 5), Date.class);
                df.setPropertyDataSource(dfProp);
            }
        });
        b.setId("set-by-ds");
        addComponent(b);

        b = new Button(
                "Set date to 27.8.2005 by changing a new property data source from null, ds attached before value setting.");
        b.setId("set-via-ds");
        b.addListener(new ClickListener() {

            @Override
            @SuppressWarnings("deprecation")
            public void buttonClick(ClickEvent event) {
                log.log("Setting object property (with value null) to datefield and set value of property to 27.8.2005");
                ObjectProperty<Date> dfProp = new ObjectProperty<Date>(null,
                        Date.class);
                df.setPropertyDataSource(dfProp);
                dfProp.setValue(new Date(2005 - 1900, 8 - 1, 27));
            }
        });
        addComponent(b);

        b = new Button("Check value");
        b.setId("check-value");
        b.addListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                log.log("Checking state");
                checkEmpty();
            }
        });
        addComponent(b);
    }

    private void checkEmpty() {
        Object value = df.getValue();
        if (value instanceof Date) {
            value = formatter.format(df.getValue());
        }

        log.log("DateField value is now " + value);
        // log.log("DateField value is now " + df.getValue());
        log.log("isEmpty: " + df.isEmpty() + ", isValid: " + df.isValid());
    }

    @Override
    protected String getDescription() {
        return "Tests the isEmpty() and isValid() functionality of a DateField. The field is required and has no other validators."
                + "IsEmpty() should return true when the field is truly empty i.e. contains no text, no matter how the field has been made empty. If the field contains any text, isEmpty() should return false."
                + "IsValid() should in this case return true if the field is not empty and vice versa.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5277;
    }

}
