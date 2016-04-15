package com.vaadin.tests.components.accordion;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.NativeButton;

public class AccordionPrimaryStylenames extends TestBase {

    @Override
    protected void setup() {
        final Accordion acc = new Accordion();
        acc.addComponent(new NativeButton("First tab"));
        acc.addComponent(new NativeButton("Second tab"));
        acc.setPrimaryStyleName("my-accordion");
        addComponent(acc);

        addComponent(new Button("Set primary stylename",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        acc.setPrimaryStyleName("my-second-accordion");
                    }
                }));
    }

    @Override
    protected String getDescription() {
        return "Accordion should work with primary stylenames both initially and dynamically";
    }

    @Override
    protected Integer getTicketNumber() {
        return 9900;
    }

}
