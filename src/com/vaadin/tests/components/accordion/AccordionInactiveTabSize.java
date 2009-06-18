package com.vaadin.tests.components.accordion;

import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.TextField;
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
        SplitPanel sp = new SplitPanel(SplitPanel.ORIENTATION_HORIZONTAL);
        sp.setSizeFull();

        Accordion acc = new Accordion();

        Tab tab1 = acc.addTab(new TextField("first field"));
        tab1.setCaption("First tab");

        Tab tab2 = acc.addTab(new TextField("second field"));
        tab2.setCaption("Second tab");

        acc.setSizeFull();

        sp.addComponent(acc);
        addComponent(sp);

    }
}
