package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.ComboBox;

public class PopUpWidth extends TestBase {

    @Override
    protected void setup() {

        addComponent(createComboBox("Do not touch this"));
        addComponent(createComboBox(
                "Browse this (check that width does not change)"));
    }

    private ComboBox<Integer> createComboBox(String caption) {
        ComboBox<Integer> cb = new ComboBox<>(caption);
        List<Integer> items = new ArrayList<>();
        for (int i = 1; i < 200 + 1; i++) {
            items.add(i);
        }
        cb.setItems(items);
        cb.setItemIconProvider(
                item -> new ThemeResource("../runo/icons/16/users.png"));
        cb.setItemCaptionProvider(item -> "Item " + item);
        return cb;
    }

    @Override
    protected String getDescription() {
        return "Check that width of popup or combobox does not change when paging.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 7013;
    }

}
