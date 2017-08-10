package com.vaadin.tests.components.datefield;

import java.util.Date;
import java.util.Locale;

import com.vaadin.data.Property;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.PopupDateField;

public class PopupDateFieldWeekDays extends AbstractTestUI {

    private static final Locale localeFI = new Locale("fi", "FI");

    @Override
    protected void setup(VaadinRequest request) {

        final PopupDateField dateTimeField = new PopupDateField();
        dateTimeField.setValue(new Date(1999, 12, 1, 12, 00));
        dateTimeField.setShowISOWeekNumbers(true);
        dateTimeField.setLocale(localeFI);

        final CheckBox weekNumbersToggle = new CheckBox("Toggle week numbers",
                dateTimeField.isShowISOWeekNumbers());
        weekNumbersToggle
                .addValueChangeListener(new Property.ValueChangeListener() {
                    @Override
                    public void valueChange(Property.ValueChangeEvent event) {
                        dateTimeField.setShowISOWeekNumbers(
                                weekNumbersToggle.getValue());
                    }
                });

        Button toEnglish = new Button("Change locale",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        dateTimeField.setLocale(Locale.ENGLISH);
                    }
                });
        toEnglish.setId("english");
        Button toFinnish = new Button("Change locale",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(Button.ClickEvent event) {
                        dateTimeField.setLocale(localeFI);

                    }
                });
        toFinnish.setId("finnish");

        addComponent(dateTimeField);
        addComponent(weekNumbersToggle);
        addComponent(new HorizontalLayout(toEnglish, toFinnish));
    }
}
