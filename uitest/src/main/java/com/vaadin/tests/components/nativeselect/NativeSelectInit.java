package com.vaadin.tests.components.nativeselect;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.NativeSelect;

/**
 * @author Vaadin Ltd
 *
 */
public class NativeSelectInit extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        NativeSelect<String> select = new NativeSelect<>();
        select.setItems("Foo", "Bar");
        select.setValue("Bar");
        addComponent(select);
    }

}
