package com.vaadin.tests.layouts.layouttester;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractLayout;

/**
 *
 * @author Vaadin Ltd
 */
public class BaseComponentSizing extends BaseLayoutTestUI {

    public BaseComponentSizing(Class<? extends AbstractLayout> layoutClass) {
        super(layoutClass);
    }

    @Override
    protected void setup(VaadinRequest request) {
        init();
        getLayoutForLayoutSizing("component");
        super.setup(request);
    }
}
