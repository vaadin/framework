package com.vaadin.tests.components.combobox;

import java.util.Arrays;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.ComboBox;

public class ComboBoxSuggestionPageLength extends AbstractReindeerTestUI {

    private static List<String> items = Arrays.asList("abc", "cde", "efg",
            "ghi", "ijk");

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox<String> cb = new ComboBox<>("Page length 0", items);
        cb.setPageLength(0);
        addComponent(cb);

        cb = new ComboBox<>("Page length 2", items);
        cb.setPageLength(2);
        addComponent(cb);
    }

    @Override
    protected String getTestDescription() {
        return "Filtering should also work when page length is set to zero.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14509;
    }

}
