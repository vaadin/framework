package com.vaadin.tests.components.accordion;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;

/**
 * Test UI for Accordion: tabs should stay selectable after remove tab.
 *
 * @since 7.2
 * @author Vaadin Ltd
 */
public class AccordionRemoveTab extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Accordion tabs = new Accordion();
        addComponent(tabs);
        tabs.setHeight(300, Unit.PIXELS);
        final VerticalLayout one = new VerticalLayout();
        one.setCaption("One");
        one.addComponent(new Label("On first tab"));
        tabs.addTab(one);
        VerticalLayout two = new VerticalLayout();
        two.setCaption("Two");
        two.addComponent(new Label("On second tab"));
        tabs.addTab(two);

        tabs.setSelectedTab(two);

        VerticalLayout l = new VerticalLayout();
        l.addComponent(new Label("On third tab"));
        Tab last = tabs.addTab(l);
        last.setCaption("Three");

        addButton("Remove First",
                event -> tabs.removeComponent(tabs.iterator().next()));
    }

    @Override
    protected String getTestDescription() {
        return "Tabs should stay selectable after remove tab.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11366;
    }

}
