package com.vaadin.tests.layouts.layouttester.GridLayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.TestDateField;
import com.vaadin.ui.AbstractOrderedLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.NativeSelect;
import com.vaadin.v7.ui.TextField;

public class GridLayoutRegError extends GridBaseLayoutTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        buildLayout();
        super.setup(request);
    }

    private void buildLayout() {

        layout.addComponent(createLabelsFields(Label.class, true, ""));
        layout.addComponent(createLabelsFields(Button.class, true, ""));
        layout.addComponent(createLabelsFields(TabSheet.class, true, ""));
        layout.addComponent(createLabelsFields(TextField.class, true, ""));

        layout.addComponent(createLabelsFields(ComboBox.class, true, ""));
        layout.addComponent(createLabelsFields(TestDateField.class, true, ""));
        layout.addComponent(createLabelsFields(NativeSelect.class, true, ""));
        layout.addComponent(createLabelsFields(CheckBox.class, true, ""));

    }

    @Override
    protected void setDefaultForVertical(AbstractOrderedLayout l1,
            AbstractOrderedLayout l2) {
        setLayoutMeasures(l1, l2, "800px", "800px");
    }
}
