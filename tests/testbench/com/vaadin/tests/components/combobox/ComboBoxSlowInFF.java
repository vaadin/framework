package com.vaadin.tests.components.combobox;

import com.vaadin.data.Item;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Table;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class ComboBoxSlowInFF extends TestBase {

    @Override
    protected void setup() {
        VerticalLayout lo = new VerticalLayout();
        lo.setSizeFull();
        final Table t = new Table();
        t.setSizeFull();
        for (int i = 0; i < 5; i++) {
            t.addContainerProperty("test" + i, Component.class, null);
        }

        Button fill = new Button("fill it");
        fill.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                t.removeAllItems();
                for (int i = 0; i < 200; i++) {
                    Item item = t.addItem(i);
                    for (int j = 0; j < 5; j++) {
                        item.getItemProperty("test" + j).setValue(
                                createComponent(i, j));
                    }
                }
            }
        });
        lo.addComponent(fill);
        lo.addComponent(t);
        lo.setExpandRatio(t, 1.0F);
        addComponent(lo);
    }

    private Component createComponent(int x, int y) {
        ComboBox box = new ComboBox();
        // box.setMultiSelect(true);
        box.addContainerProperty("name", String.class, "");
        box.setItemCaptionPropertyId("name");
        for (int ix = 0; ix < 20; ix++) {
            box.addItem(x + 20 * y + ix).getItemProperty("name")
                    .setValue("" + x + ", " + y + " " + ix);
        }
        box.setValue(x + 20 * y);
        return box;
    }

    @Override
    protected String getDescription() {
        return "FF is very slow when rendering many ComboBoxes in a table";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3663;
    }

}
