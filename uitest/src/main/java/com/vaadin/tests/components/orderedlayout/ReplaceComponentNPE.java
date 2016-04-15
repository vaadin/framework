package com.vaadin.tests.components.orderedlayout;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.VerticalLayout;

public class ReplaceComponentNPE extends TestBase {

    @Override
    protected String getDescription() {
        return "Clicking 'ReplaceComponent' should replace the 'Button' button with a VericalLayout, and move the button inside the verticalLayout. Visually this can be seen by the added margins of the VerticalLayout.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3195;
    }

    final Button button = new Button("Button");
    final VerticalLayout outer = new VerticalLayout();

    @Override
    protected void setup() {
        outer.setMargin(true);

        Button changer = new Button("ReplaceComponent");
        changer.addListener(new Button.ClickListener() {
            @Override
            public void buttonClick(ClickEvent event) {
                getLayout().replaceComponent(button, outer);
                outer.addComponent(button);
            }
        });

        getLayout().addComponent(button);
        getLayout().addComponent(changer);

    }

}
