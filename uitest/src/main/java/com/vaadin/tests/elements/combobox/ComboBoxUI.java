package com.vaadin.tests.elements.combobox;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.ThemeResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class ComboBoxUI extends AbstractTestUI {

    public static final List<String> currencies = new ArrayList<String>();
    static {
        currencies.add("GBP");
        currencies.add("EUR");
        currencies.add("USD");
    }

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox<String> comboBox = new ComboBox<>("NullAllowedComboBox",
                currencies);
        addComponent(comboBox);

        comboBox = new ComboBox<>("NullForbiddenComboBox", currencies);
        comboBox.setEmptySelectionAllowed(false);
        addComponent(comboBox);

        comboBox = new ComboBox<>("With icons", currencies);
        comboBox.setId("with-icons");
        comboBox.setItemIconGenerator(item -> {
            if (item.equals("EUR")) {
                return new ThemeResource("shared/img/spinner.gif");
            }
            return new ThemeResource("notfound.png");
        });
        addComponent(comboBox);
    }

    @Override
    protected String getTestDescription() {
        return "When calling ComboBoxElement.selectByText(String) several times, the input text should be cleared every time, instead of being appended";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14404;
    }

}
