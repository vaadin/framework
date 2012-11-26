package com.vaadin.tests.components.uitest;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;

public class UIScrollTest extends TestBase {

    @Override
    protected void setup() {
        // Set layout to high enough to get scroll.
        getLayout().setHeight("2250px");
        addComponent(new Button("scoll to 1000px", new Button.ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                UI.getCurrent().setScrollable(true);
                UI.getCurrent().setScrollTop(1000);
            }
        }));
        addComponent(new Label("This label is halfway down"));
    }

    @Override
    protected String getDescription() {
        return "Windows can be programmatically scrolled";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9952;
    }

}
