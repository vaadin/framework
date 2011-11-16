package com.vaadin.tests.components.combobox;

import java.util.Arrays;

import com.vaadin.event.FieldEvents;
import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Window;

public class ComboBoxSuggestionOnDetach extends TestBase {

    @Override
    protected void setup() {
        final Window popup = new Window();
        ComboBox comboBox = new ComboBox("Combo box", Arrays.asList("Option 1",
                "Option 2", "Option 3"));
        comboBox.addListener(new FieldEvents.FocusListener() {
            public void focus(FocusEvent event) {
                popup.close();
            }
        });
        popup.addComponent(comboBox);

        popup.setSizeUndefined();
        popup.getContent().setSizeUndefined();
        popup.center();

        getMainWindow().addWindow(popup);
    }

    @Override
    protected String getDescription() {
        return "Click the arrow to open the combo box suggestion list. When the box is focused, the window is closed and the suggestion popup of the combo box should also be closed";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(7536);
    }

}
