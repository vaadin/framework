package com.vaadin.tests.components.combobox;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.Page;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;

import java.util.Arrays;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class ComboboxPopupPosition extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox<String> nameComboBox = new ComboBox<>("Long items");
        nameComboBox.setPopupWidth(null);
        nameComboBox.setItems(Arrays.asList("First Very long item to add",
                "Second very long item to add", "Third very long item to add"));
        nameComboBox.addValueChangeListener(event -> {
            Notification.show(nameComboBox.getValue());
        });
        addComponent(nameComboBox);
        Page.getCurrent().getStyles()
                .add(".v-slot.v-slot-positionRight {float:right;}");
        Page.getCurrent().getStyles()
                .add(".removePadding {padding: 0px !important;}");

        getLayout().setMargin(false);
        getLayout().setSpacing(false);

        getLayout().getParent().addStyleName("removePadding");
        nameComboBox.addStyleName("positionRight");
    }
}
