package com.vaadin.tests.components.combobox;

import java.util.Arrays;
import java.util.Iterator;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;

public class ComboBoxWithinHorizontalLayout extends AbstractTestUI {

    private ComboBox<String> comboBox;

    @Override
    protected void setup(VaadinRequest request) {
        comboBox = new ComboBox<>();
        comboBox.setWidth("100%");
        comboBox.setPopupWidth(null);
        populateWithShortItems();

        HorizontalLayout content = new HorizontalLayout(new Button("1"),
                new Button("2"), comboBox, new Button("3"));
        content.setWidth("100%");
        content.setExpandRatio(comboBox, 2.0f);
        Iterator<Component> i = content.iterator();
        while (i.hasNext()) {
            content.setComponentAlignment(i.next(), Alignment.BOTTOM_RIGHT);
        }

        getLayout().setSpacing(true);
        addComponent(content);
        addComponent(new Button("Toggle items", e -> {
            if ("Short items".equals(comboBox.getCaption())) {
                populateWithLongItems();
            } else {
                populateWithShortItems();
            }
        }));
        addComponent(new Button("Toggle spacing", e -> {
            content.setSpacing(!content.isSpacing());
        }));
        addComponent(new Button("Toggle expand ratio", e -> {
            if (content.getExpandRatio(comboBox) > 0.0f) {
                content.setExpandRatio(comboBox, 0.0f);
            } else {
                content.setExpandRatio(comboBox, 2.0f);
            }
        }));
    }

    private void populateWithLongItems() {
        comboBox.setCaption("Long items");
        comboBox.setItems(Arrays.asList(
                "First very, very, very, very, very long item to add",
                "Second very long item to add", "Third very long item to add"));
    }

    private void populateWithShortItems() {
        comboBox.setCaption("Short items");
        comboBox.setItems(Arrays.asList("short1", "short2", "short3"));
    }

    @Override
    protected Integer getTicketNumber() {
        return 11718;
    }

    @Override
    protected String getTestDescription() {
        return "ComboBox within HorizontalLayout should not get incorrect "
                + "intermediate popup positions that cause flickering.";
    }
}
