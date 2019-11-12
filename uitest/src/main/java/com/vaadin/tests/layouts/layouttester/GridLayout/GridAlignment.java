package com.vaadin.tests.layouts.layouttester.GridLayout;

import com.vaadin.server.VaadinRequest;

public class GridAlignment extends GridBaseLayoutTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        buildLayout();
        super.setup(request);
        layout.setSizeFull();
    }

    /**
     * Build Layout for test
     */
    private void buildLayout() {
        layout.setColumns(3);
        layout.setRows(3);
        for (int i = 0; i < components.length; i++) {
            layout.addComponent(components[i]);
            layout.setComponentAlignment(components[i], alignments[i]);
        }
    }
}
