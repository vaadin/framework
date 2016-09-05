package com.vaadin.tests.components.abstractlisting;

import java.util.LinkedHashMap;
import java.util.stream.IntStream;

import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.AbstractListing;

public abstract class AbstractListingTestUI<T extends AbstractListing<Object, ?>, V>
        extends AbstractComponentTest<T> {

    @Override
    protected void createActions() {
        super.createActions();
        createItemsSelect();
        createSelectionSelect();
    }

    protected void createItemsSelect() {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<>();
        for (int i = 0; i <= 10; i++) {
            options.put(String.valueOf(i), i);
        }
        options.put("20", 20);
        options.put("100", 100);
        options.put("1000", 1000);
        options.put("10000", 10000);
        options.put("100000", 100000);

        createSelectAction("Items", "Data source", options, "20",
                (c, number, data) -> {
                    c.setItems(createItems(number));
                });
    }

    protected void createSelectionSelect() {
        LinkedHashMap<String, String> options = new LinkedHashMap<>();
        options.put("None", null);
        options.put("Item 0", "Item 0");
        options.put("Item 1", "Item 1");
        options.put("Item 2", "Item 2");
        options.put("Item 10", "Item 10");
        options.put("Item 100", "Item 100");

        createSelectAction("Select", "Selection", options, "None",
                (c, selected, data) -> {
                    if (selected != null) {
                        c.select(selected);
                    } else {
                        c.getSelectedItems().forEach(c::deselect);
                    }
                });
    }

    protected Object[] createItems(int number) {
        return IntStream.rangeClosed(0, number)
                .mapToObj(i -> "Item " + i)
                .toArray();
    }
}
