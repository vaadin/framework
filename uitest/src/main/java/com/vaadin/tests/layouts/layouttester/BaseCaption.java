package com.vaadin.tests.layouts.layouttester;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.TabSheet;
import com.vaadin.v7.ui.ComboBox;

public class BaseCaption extends BaseLayoutTestUI {

    /**
     * @param layoutClass
     */
    public BaseCaption(Class<? extends AbstractLayout> layoutClass) {
        super(layoutClass);
    }

    @Override
    protected void setup(VaadinRequest request) {
        init();
        l1.addComponent(createLabelsFields(ComboBox.class, true, ""));
        l2.addComponent(createLabelsFields(TabSheet.class, false, ""));
        super.setup(request);
    }
}
