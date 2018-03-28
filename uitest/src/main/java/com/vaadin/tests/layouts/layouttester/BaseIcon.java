package com.vaadin.tests.layouts.layouttester;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Link;
import com.vaadin.ui.TabSheet;
import com.vaadin.v7.ui.ComboBox;
import com.vaadin.v7.ui.TextField;

/**
 *
 * @since
 * @author Vaadin Ltd
 */
public class BaseIcon extends BaseLayoutTestUI {
    /**
     * @param layoutClass
     */
    public BaseIcon(Class<? extends AbstractLayout> layoutClass) {
        super(layoutClass);
    }

    @Override
    protected void setup(VaadinRequest request) {
        init();
        l1.addComponent(createLabelsFields(TextField.class, true, ""));
        l1.addComponent(createLabelsFields(Label.class, true, ""));
        l1.addComponent(createLabelsFields(Button.class, true, ""));
        l2.addComponent(createLabelsFields(ComboBox.class, true, ""));
        l2.addComponent(createLabelsFields(Link.class, true, ""));
        l2.addComponent(createLabelsFields(TabSheet.class, true, ""));
        super.setup(request);
    }
}
