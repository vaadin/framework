package com.vaadin.tests.components.combobox;

import java.util.Arrays;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.VerticalLayout;

public class ComboBoxAtRightEdge extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox<String> comboBox = new ComboBox<>("Long items?");
        comboBox.setPopupWidth(null);
        comboBox.setItems(Arrays.asList("First Very long item to add",
                "Second very long item to add", "Third very long item to add"));
        comboBox.addStyleName("positionRight");

        addComponent(comboBox);
        getLayout().setComponentAlignment(comboBox, Alignment.BOTTOM_RIGHT);

        ((VerticalLayout) getLayout().getParent()).setMargin(false);
    }

    @Override
    protected Integer getTicketNumber() {
        return 11718;
    }

    @Override
    protected String getTestDescription() {
        return "ComboBox popup should fit completely in view, margin/border/padding included.";
    }
}
