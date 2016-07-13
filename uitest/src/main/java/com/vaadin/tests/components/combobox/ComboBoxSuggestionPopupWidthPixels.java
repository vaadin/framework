package com.vaadin.tests.components.combobox;

import java.util.Arrays;
import java.util.List;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.ComboBox;

public class ComboBoxSuggestionPopupWidthPixels extends AbstractTestUI {

    private static List<String> items = Arrays
            .asList("abc",
                    "cde",
                    "efg",
                    "ghi",
                    "ijk",
                    "more items 1",
                    "more items 2",
                    "more items 3",
                    "Ridicilously long item caption so we can see how the ComboBox displays ridicilously long captions in the suggestion pop-up",
                    "more items 4", "more items 5", "more items 6",
                    "more items 7");

    @Override
    protected void setup(VaadinRequest request) {
        ComboBox pixels = new ComboBox(
                "200px wide ComboBox with 300px wide suggestion popup", items);
        pixels.addStyleName("pixels");
        pixels.setWidth("200px");
        pixels.setPopupWidth("300px");
        addComponent(pixels);

    }

    @Override
    protected String getTestDescription() {
        return "Suggestion pop-up's width should 300px";
    }

    @Override
    protected Integer getTicketNumber() {
        return 19685;
    }

}
