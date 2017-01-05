package com.vaadin.tests.components.combobox;

import java.util.Arrays;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.ComboBox;

public class ComboBoxSuggestionPopupWidthPercentage
        extends AbstractReindeerTestUI {

    private static List<String> items = Arrays.asList("abc", "cde", "efg",
            "ghi", "ijk", "more items 1", "more items 2", "more items 3",
            "Ridicilously long item caption so we can see how the ComboBox displays ridicilously long captions in the suggestion pop-up",
            "more items 4", "more items 5", "more items 6", "more items 7");

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox<String> percentage = new ComboBox<>(
                "200px wide ComboBox with 200% wide suggestion popup", items);
        percentage.addStyleName("percentage");
        percentage.setWidth("200px");
        percentage.setPopupWidth("200%");
        addComponent(percentage);

    }

    @Override
    protected String getTestDescription() {
        return "Suggestion pop-up's width should be 200% of the ComboBox itself (400px)";
    }

    @Override
    protected Integer getTicketNumber() {
        return 19685;
    }

}
