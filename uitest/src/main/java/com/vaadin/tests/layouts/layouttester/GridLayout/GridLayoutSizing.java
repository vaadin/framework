package com.vaadin.tests.layouts.layouttester.GridLayout;

import com.vaadin.server.VaadinRequest;

public class GridLayoutSizing extends GridBaseLayoutTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        getLayoutForLayoutSizing("layout");
        super.setup(request);
    }
}
