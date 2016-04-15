package com.vaadin.tests.components.combobox;

import java.util.Arrays;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.TextArea;

public class ComboboxInPopupViewWithItems extends TestBase {

    @Override
    protected void setup() {
        addComponent(new TextArea("Some component"));
        addComponent(new PopupView(new PopupContent()));

    }

    @Override
    protected String getDescription() {
        return "Combobox popup should be in the correct place even when it is located inside a PopupView";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9768;
    }

    class PopupContent implements PopupView.Content {

        private final ComboBox cb = new ComboBox(null, Arrays.asList("Item 1",
                "Item 2", "Item 3"));

        @Override
        public String getMinimizedValueAsHTML() {
            return "click here";
        }

        @Override
        public Component getPopupComponent() {
            return cb;
        }
    }
}
