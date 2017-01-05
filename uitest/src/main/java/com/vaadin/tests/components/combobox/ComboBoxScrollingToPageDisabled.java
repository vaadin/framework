package com.vaadin.tests.components.combobox;

import java.util.ArrayList;
import java.util.List;

import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;

public class ComboBoxScrollingToPageDisabled
        extends ComponentTestCase<ComboBox> {

    private static final Object CAPTION = "caption";

    @Override
    protected Class<ComboBox> getTestClass() {
        return ComboBox.class;
    }

    @Override
    protected void initializeComponents() {
        ComboBox<String> s = createSelect(null);
        s.setScrollToSelectedItem(false);
        populate(s, 100);
        s.setValue("Item 50");
        addTestComponent(s);
    }

    private void populate(ComboBox s, int nr) {
        List<String> items = new ArrayList<>();
        for (int i = 0; i < nr; i++) {
            items.add("Item " + i);
        }
        s.setItems(items);
    }

    private ComboBox<String> createSelect(String caption) {
        final ComboBox<String> cb = new ComboBox<>();
        cb.setCaption(caption);
        cb.addValueChangeListener(event -> Notification
                .show("Value now:" + cb.getValue() + " " + cb.getValue()));
        return cb;
    }

    @Override
    protected String getTestDescription() {
        return "Test that selected value appears on the client "
                + "side even though setScrollToSelectedItem(false) "
                + "has been called. Textbox should contain 'Item 50'.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 16673;
    }

}
