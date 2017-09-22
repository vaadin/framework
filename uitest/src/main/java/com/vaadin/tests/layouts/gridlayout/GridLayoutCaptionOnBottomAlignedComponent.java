package com.vaadin.tests.layouts.gridlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.TextField;

public class GridLayoutCaptionOnBottomAlignedComponent extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        GridLayout layout = new GridLayout();
        layout.setHeight("200px");
        layout.setWidth("100%");

        TextField component = new TextField("Oh Caption My Caption");
        layout.addComponent(component);
        layout.setComponentAlignment(component, Alignment.BOTTOM_CENTER);

        addComponent(layout);

        Button realign = new Button("Realign", evt -> {
            layout.setComponentAlignment(component, Alignment.TOP_LEFT);
        });
        addComponent(realign);
    }
}
