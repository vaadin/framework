package com.vaadin.tests.components.combobox;

import java.util.ArrayList;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.tests.components.ComponentTestCase;
import com.vaadin.ui.Button;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class ComboBoxScrollingToPageDisabled
        extends ComponentTestCase<ComboBox> {

    private static final Object CAPTION = "caption";

    @Override
    protected Class<ComboBox> getTestClass() {
        return ComboBox.class;
    }

    @Override
    protected void initializeComponents() {
        final ComboBox s = createSelect(null);
        s.setScrollToSelectedItem(false);
        populate(s, 100);
        final Object selection = new ArrayList<Object>(s.getItemIds()).get(50);
        s.setValue(selection);
        addTestComponent(s);

        Button button = new Button("Select first");
        button.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                s.setValue(s.getItemIds().iterator().next());
            }
        });
        addComponent(button);

        Button button2 = new Button("Select index 50");
        button2.addClickListener(new ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                s.setValue(selection);
            }
        });
        addComponent(button2);
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
                + "has been called. Textbox should contain 'Item 50'.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 16673;
    }

}
