package com.vaadin.tests.components.grid;

import com.vaadin.server.VaadinRequest;
import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Grid;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class GridClientMemoryLeak extends AbstractTestUI {

    private static final String INSTRUCTIONS = "This UI is for manually testing that the client side grid does not leak memory. "
            + "Steps to take:\n"
            + "\t1. Click the newGrid button 1-n times\n"
            + "\t2. Capture a JS heap dump in your browser\n"
            + "\t3. The heap dump should only contain 1 instance of each of the following:\n"
            + "\t\tGrid, GridKeyDownEvent, GridKeyPressEvent, GridKeyUpEvent, GridClickEvent, GridDoubleClickEvent";

    @Override
    protected void setup(VaadinRequest request) {
        final Label instructionLabel = new Label(INSTRUCTIONS,
                ContentMode.PREFORMATTED);
        final VerticalLayout layout = new VerticalLayout();
        final Button btn = new Button("newGrid");
        btn.addClickListener(new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                layout.removeComponent(layout.getComponent(1));
                layout.addComponent(new Grid<String>());
            }
        });
        layout.addComponent(instructionLabel);
        layout.addComponent(btn);
        layout.addComponent(new Grid<String>());
        addComponent(layout);
    }
}
