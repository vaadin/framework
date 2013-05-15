package com.vaadin.tests.components.abstractfield;

import java.util.Arrays;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.NativeButton;
import com.vaadin.ui.OptionGroup;
import com.vaadin.ui.TextField;

public class FieldFocusOnClick extends TestBase {

    @Override
    protected void setup() {

        addComponent(new TextField(null, "TextField"));
        addComponent(new CheckBox("CheckBox"));
        addComponent(new OptionGroup(null,
                Arrays.asList("Option 1", "Option 2")));
        addComponent(new NativeButton("NativeButton"));
    }

    @Override
    protected String getDescription() {
        return "Webkit doesn't focus non-text input elements when clicked";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11854;
    }
}
