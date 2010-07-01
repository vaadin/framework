package com.vaadin.tests.components.datefield;

import java.sql.Date;
import java.util.Locale;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.components.TestBase;
import com.vaadin.tests.util.Log;
import com.vaadin.ui.Button;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class DateFieldEmptyValid extends TestBase {

    private Log log;

    private MyDateField df;

    public class MyDateField extends PopupDateField {
        @Override
        public boolean isEmpty() {
            return super.isEmpty();
        }

    }

    @Override
    protected void setup() {
        addComponent(new Label("<br/><br/>", Label.CONTENT_XHTML));
        log = new Log(5);
        log.setNumberLogRows(true);
        addComponent(log);
        df = new MyDateField();
        df.setRequired(true);
        df.setLocale(new Locale("fi", "FI"));
        df.setValue(new Date(100000000000L));
        df.setImmediate(true);
        df.setResolution(DateField.RESOLUTION_DAY);
        df.addListener(new ValueChangeListener() {

            public void valueChange(ValueChangeEvent event) {
                checkEmpty();

            }

        });
        addComponent(df);
        checkEmpty();
        Button b = new Button("Clear date");
        b.addListener(new ClickListener() {

            public void buttonClick(ClickEvent event) {
                df.setValue(null);
            }
        });
        addComponent(b);

        b = new Button("Set date to 4.5.1990");
        b.addListener(new ClickListener() {

            public void buttonClick(ClickEvent event) {
                df.setValue(new Date(1990 - 1900, 5 - 1, 4));
            }
        });
        addComponent(b);
    }

    private void checkEmpty() {
        log.log("DateField value is now " + df.getValue());
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
