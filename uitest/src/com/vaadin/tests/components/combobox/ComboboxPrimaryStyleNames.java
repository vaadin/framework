package com.vaadin.tests.components.combobox;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComboBox;

public class ComboboxPrimaryStyleNames extends TestBase {

    @Override
    protected void setup() {
        final ComboBox box = new ComboBox();
        box.setImmediate(true);
        box.addContainerProperty("caption", String.class, "");
        box.setItemCaptionPropertyId("caption");
        box.setPrimaryStyleName("my-combobox");

        addItem(box, "Value 1");
        addItem(box, "Value 2");
        addItem(box, "Value 3");
        addItem(box, "Value 4");

        addComponent(box);
        addComponent(new Button("Set primary style",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        box.setPrimaryStyleName("my-second-combobox");
                    }
                }));

    }

    @Override
    protected String getDescription() {
        return "Combobox should work with primary stylenames both initially and dynamically";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9901;
    }

    private void addItem(ComboBox s, String string) {
        Object id = s.addItem();
        s.getItem(id).getItemProperty("caption").setValue(string);
    }

}
