package com.vaadin.tests.components.popupview;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;

public class PopupViewLabelResized extends TestBase {

    @Override
    protected String getDescription() {
        return "When clicking on the popup view on the left, its size should not change.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3483;
    }

    @Override
    protected void setup() {
        GridLayout gl = new GridLayout(3, 1);
        gl.setSizeFull();

        Label expander = new Label();
        gl.addComponent(expander, 1, 0);
        gl.setColumnExpandRatio(1, 1);

        gl.addComponent(
                new PopupView("Click here to popup", new Label("test")), 0, 0);
        gl.addComponent(
                new PopupView("Click here to popup", new Label("test")), 2, 0);

        addComponent(gl);
    }

}
