package com.vaadin.tests.components.tabsheet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;

/**
 * Main UI class
 */
@SuppressWarnings("serial")
public class MoveComponentBetweenTabsheets extends AbstractTestUI {

    TabSheet left, right;
    private Label l1;
    private Label l2;
    private Label r1;
    private Label r2;

    void doTestOperation() {
        right.addTab(l1, "L1");
        right.setSelectedTab(l1);
    }

    @Override
    protected void setup(VaadinRequest request) {

        // TODO Auto-generated method stub
        Button button = new Button("Move L1 to the right tabsheet");
        button.addClickListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                doTestOperation();
            }
        });

        getLayout().addComponent(button);

        left = new TabSheet();
        l1 = new Label("Left 1");
        left.addTab(l1, "L1");
        l2 = new Label("Left 2");
        left.addTab(l2, "L2");
        left.setWidth("400px");

        right = new TabSheet();
        r1 = new Label("Right 1");
        right.addTab(r1, "R1");
        r2 = new Label("Right 2");
        right.addTab(r2, "R2");
        right.setWidth("400px");

        getLayout().addComponent(new HorizontalLayout(left, right));
    }

    @Override
    protected String getTestDescription() {
        return "Moving a component from a tabsheet to another sometimes causes a client-side error";
    }

    @Override
    protected Integer getTicketNumber() {
        return 10839;
    }

}
