package com.vaadin.tests.layouts.layouttester.GridLayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.TabSheet;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

public class GridIcon extends GridBaseLayoutTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        layout.addComponent(createLabelsFields(TextField.class, true, ""));
        layout.addComponent(createLabelsFields(Label.class, true, ""));
        layout.addComponent(createLabelsFields(Button.class, true, ""));
        layout.addComponent(createLabelsFields(ComboBox.class, true, ""));
        layout.addComponent(createLabelsFields(Link.class, true, ""));
        layout.addComponent(createLabelsFields(TabSheet.class, true, ""));
        super.setup(request);
    }

    @Override
    protected void setLayoutMeasures(AbstractOrderedLayout l1,
            AbstractOrderedLayout l2, String w, String h) {
        super.setLayoutMeasures(l1, l2, "400px", "-1px");
        l1.setSpacing(true);
        l2.setSpacing(true);
        mainLayout.setSpacing(true);
    }
}
