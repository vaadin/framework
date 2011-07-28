package com.vaadin.tests.components.tabsheet;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextField;

public class TabSheetDiscardsMovedComponents extends TestBase implements
        ClickListener {

    private static final long serialVersionUID = 3153820728819626096L;

    private Component c1 = new TextField("Component 1", "TextField 1");
    private Component c2 = new Button("Component 2");

    private TabSheet ts = null;
    private Button moveButton = new Button("Move components to new tabsheet",
            this);
    private Button moveAndSelectButton = new Button(
            "Move components to new tabsheet and remove previous tabsheet",
            this);

    private HorizontalLayout hl;

    @Override
    public void setup() {
        hl = new HorizontalLayout();

        hl.addComponent(moveButton);
        hl.addComponent(moveAndSelectButton);

        recreateTabSheet();

        hl.addComponent(ts);
        addComponent(hl);
    }

    private void recreateTabSheet() {
        ts = new TabSheet();
        ts.addTab(c1);
        ts.addTab(c2);
    }

    public void buttonClick(ClickEvent event) {
        if (event.getButton() == moveAndSelectButton) {
            hl.removeComponent(ts);
        }
        ts.addTab(new Label("Old tabsheet"), "Old tabsheet", null);
        recreateTabSheet();
        hl.addComponent(ts);

    }

    @Override
    protected String getDescription() {
        return "Moving components from one TabSheet to another should not cause problems. Click the second tab and then a button to move the components to another TabSheet and the caption of the Button or the contents of the TextField will disappear";
    }

    @Override
    protected Integer getTicketNumber() {
        return 2669;
    }

}
