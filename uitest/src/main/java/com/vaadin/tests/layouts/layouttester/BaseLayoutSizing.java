package com.vaadin.tests.layouts.layouttester;

import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractLayout;

public class BaseLayoutSizing extends BaseLayoutTestUI {
    /**
     * @param layoutClass
     */
    public BaseLayoutSizing(Class<? extends AbstractLayout> layoutClass) {
        super(layoutClass);
    }

    @Override
    protected void setup(VaadinRequest request) {
        init();
        getLayoutForLayoutSizing("layout");
        super.setup(request);
    }
}
