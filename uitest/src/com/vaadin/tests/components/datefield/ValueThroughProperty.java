package com.vaadin.tests.components.datefield;

import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

import com.vaadin.data.Property;
import com.vaadin.data.util.ObjectProperty;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.DateField;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupDateField;

public class ValueThroughProperty extends TestBase {
    private final Property<Date> dateProperty = new ObjectProperty<Date>(null,
            Date.class);

    @Override
    protected void setup() {
        addComponent(new Label(
                "Try to input an invalid value to the DateField, for example \"asdf\".<br />"
                        + "Then try to set DateField's value using the first button. It sets the value "
                        + "correctly (as we can see from the Label) but the client-side is not updated.<br/>"
                        + "Using second button updates value correctly on the client-side too.",
                ContentMode.XML));

        final PopupDateField df = new PopupDateField(dateProperty);
        df.setLocale(new Locale("en", "US"));
        df.setImmediate(true);
        df.setResolution(DateField.RESOLUTION_DAY);
        addComponent(df);

        Label valueLabel = new Label(df.getPropertyDataSource());
        valueLabel.setCaption("DateField's value");
        addComponent(valueLabel);

        final Calendar cal = Calendar.getInstance();
        cal.set(Calendar.YEAR, 2010);
        cal.set(Calendar.MONTH, 11);
        cal.set(Calendar.DAY_OF_MONTH, 14);
        Button setDateButton1 = new Button(
                "Set value to 12/14/10 using property", new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        dateProperty.setValue(cal.getTime());
                    }

                });
        addComponent(setDateButton1);

        Button setDateButton2 = new Button(
                "Set value to 12/14/10 using setValue", new ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        df.setValue(cal.getTime());
                    }

                });
        addComponent(setDateButton2);
    }

    @Override
    protected String getDescription() {
        return "Setting a value through a property should update the"
                + " client-side even if it contains an invalid value.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 5810;
    }

}
