package com.vaadin.tests.layouts.layouttester.GridLayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.v7.ui.ComboBox;

public class GridCaption extends GridBaseLayoutTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        layout.addComponent(createLabelsFields(Button.class, true, ""));
        layout.addComponent(createLabelsFields(Label.class, true, ""));
        layout.addComponent(createLabelsFields(ComboBox.class, true, ""));
        layout.addComponent(createLabelsFields(TabSheet.class, false, ""));
        super.setup(request);
    }

}
