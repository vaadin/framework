package com.vaadin.tests.components.combobox;

import java.util.ArrayList;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;

public class ComboBoxScrollingToPageDisabled extends
        ComponentTestCase<ComboBox> {

    private static final Object CAPTION = "caption";

    @Override
    protected Class<ComboBox> getTestClass() {
        return ComboBox.class;
    }

    @Override
    protected void initializeComponents() {
        ComboBox s = createSelect(null);
        s.setScrollToSelectedItem(false);
        populate(s, 100);
        Object selection = new ArrayList<Object>(s.getItemIds()).get(50);
        s.setValue(selection);
        addTestComponent(s);
    }

    private void populate(ComboBox s, int nr) {
        for (int i = 0; i < nr; i++) {
            addItem(s, "Item " + i);
        }
    }

    @SuppressWarnings("unchecked")
    private void addItem(ComboBox s, String string) {
        Object id = s.addItem();
        s.getItem(id).getItemProperty(CAPTION).setValue(string);

    }

    private ComboBox createSelect(String caption) {
        final ComboBox cb = new ComboBox();
        cb.setImmediate(true);
        cb.addContainerProperty(CAPTION, String.class, "");
        cb.setItemCaptionPropertyId(CAPTION);
        cb.setCaption(caption);
        cb.addValueChangeListener(new ValueChangeListener() {

            @Override
            public void valueChange(ValueChangeEvent event) {
                Notification.show("Value now:" + cb.getValue() + " "
                        + cb.getItemCaption(cb.getValue()));

            }
        });
        return cb;
    }

    @Override
    protected String getDescription() {
        return "Test that selected value appears on the client "
                + "side even though setScrollToSelectedItem(false) "
                + "has been called. Textbox should containe 'Item 50'.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 16673;
    }

}
