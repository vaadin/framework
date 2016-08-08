package com.vaadin.tests.components.accordion;

import com.vaadin.legacy.ui.LegacyTextField;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.TabSheet.Tab;

public class AccordionInactiveTabSize extends TestBase {

    @Override
    protected String getDescription() {
        return "Select the second tab and move the splitter to the right. Both the inactive and the active tab should be resized.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3065;
    }

    @Override
    protected void setup() {
        HorizontalSplitPanel sp = new HorizontalSplitPanel();
        sp.setWidth("100%");
        sp.setHeight("100px");

        Accordion acc = new Accordion();

        Tab tab1 = acc.addTab(new LegacyTextField("first field"));
        tab1.setCaption("First tab");

        Tab tab2 = acc.addTab(new LegacyTextField("second field"));
        tab2.setCaption("Second tab");

        acc.setSizeFull();

        sp.addComponent(acc);
        addComponent(sp);

    }
}
