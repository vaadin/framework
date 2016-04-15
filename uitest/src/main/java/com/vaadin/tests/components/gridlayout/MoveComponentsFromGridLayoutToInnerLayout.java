package com.vaadin.tests.components.gridlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalLayout;

public class MoveComponentsFromGridLayoutToInnerLayout extends AbstractTestUI {

    protected Button testButton;
    private GridLayout gl;
    protected ComponentContainer vl;

    @Override
    protected void setup(VaadinRequest request) {
        gl = new GridLayout();
        gl.setHideEmptyRowsAndColumns(true);
        gl.setWidth("200px");
        gl.setHeight("200px");

        testButton = new Button("Click to move to inner layout",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        vl.addComponent(testButton);
                    }
                });

        gl.addComponent(testButton);

        vl = new VerticalLayout();
        vl.addComponent(new Label("I'm inside the inner layout"));
        gl.addComponent(vl);

        addComponent(gl);

        Button b = new Button("Repaint inner layout",
                new Button.ClickListener() {

                    @Override
                    public void buttonClick(ClickEvent event) {
                        vl.markAsDirty();
                    }
                });

        addComponent(b);
    }

    @Override
    protected String getTestDescription() {
        return "Click the first button to move it from an outer layout to an inner. Then click the second button to repaint the inner layout.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6060;
    }

}
