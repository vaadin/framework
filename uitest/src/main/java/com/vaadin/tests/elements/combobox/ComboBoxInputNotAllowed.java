package com.vaadin.tests.elements.combobox;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Label;

public class ComboBoxInputNotAllowed extends AbstractTestUI {

    public static final String ITEM_ON_FIRST_PAGE = "item 2";
    public static final String ITEM_ON_SECOND_PAGE = "item 19";
    public static final String ITEM_ON_LAST_PAGE = "item 30";
    public static final String ITEM_LAST_WITH_PARENTHESIS = "item (last)";

    public static final List<String> ITEMS = new ArrayList<String>();
    static {
        for (int i = 1; i <= 30; i++) {
            ITEMS.add("item " + i);
        }
        ITEMS.add(ITEM_LAST_WITH_PARENTHESIS);
    }

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox<String> comboBox = new ComboBox<>("", ITEMS);
        comboBox.setTextInputAllowed(false);
        comboBox.addValueChangeListener(e -> {
            addComponent(new Label("Value is now: " + e.getValue()));
        });

        addComponent(comboBox);
    }

    @Override
    protected String getTestDescription() {
        return "ComboBoxElement.selectByText(String) selects only first item when setTextInputAllowed set to false ";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14486;
    }

}
