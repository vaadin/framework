package com.vaadin.tests.components.abstractlisting;

import java.util.LinkedHashMap;
import java.util.stream.IntStream;

import com.vaadin.annotations.Widgetset;
import com.vaadin.tests.components.AbstractComponentTest;
import com.vaadin.ui.AbstractListing;

@Widgetset("com.vaadin.DefaultWidgetSet")
public abstract class AbstractListingTestUI<T extends AbstractListing<Object>>
        extends AbstractComponentTest<T> {

    @Override
    protected void createActions() {
        super.createActions();
        createItemsMenu();
    }

    protected void createItemsMenu() {
        LinkedHashMap<String, Integer> options = new LinkedHashMap<>();
        for (int i = 0; i <= 10; i++) {
            options.put(String.valueOf(i), i);
        }
        options.put("20", 20);
        options.put("100", 100);
        options.put("1000", 1000);
        options.put("10000", 10000);
        options.put("100000", 100000);

        createSelectAction("Items", "Data provider", options, "20",
                (c, number, data) -> {
                    c.setItems(createItems(number));
                });
    }

    protected Object[] createItems(int number) {
        return IntStream.range(0, number).mapToObj(i -> "Item " + i).toArray();
    }
}
