package com.vaadin.tests.components.nativeselect;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.NativeSelect;

/**
 * @author Vaadin Ltd
 *
 */
@Widgetset("com.vaadin.DefaultWidgetSet")
public class NativeSelectDisabled extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        NativeSelect<String> select = new NativeSelect<>();
        select.setItems("Foo", "Bar");
        select.setValue("Bar");
        select.setEnabled(false);
        addButton("Set Enabled/Disabled", event -> {
            select.setEnabled(!select.isEnabled());
        });
        addComponent(select);
    }

}
