package com.vaadin.tests.components.splitpanel;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;

public class SplitPanelSwapComponents extends TestBase {

    @Override
    protected void setup() {
        final HorizontalSplitPanel hsplit = new HorizontalSplitPanel();
        hsplit.setWidth("300px");
        hsplit.setHeight("300px");
        hsplit.setSecondComponent(new Label("A label"));
        hsplit.setFirstComponent(new Label("Another label"));
        getLayout().addComponent(hsplit);

        Button swap = new Button("Swap components", new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                Component first = hsplit.getFirstComponent();
                hsplit.removeComponent(first);

                Component second = hsplit.getSecondComponent();
                hsplit.removeComponent(second);

                hsplit.setFirstComponent(second);
                hsplit.setSecondComponent(first);
            }
        });

        getLayout().addComponent(swap);

    }

    @Override
    protected String getDescription() {
        return "Swapping components should work";
    }

    @Override
    protected Integer getTicketNumber() {
        return 6171;
    }

}
