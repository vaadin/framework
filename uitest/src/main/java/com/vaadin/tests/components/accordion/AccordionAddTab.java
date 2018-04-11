package com.vaadin.tests.components.accordion;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.TabSheet.Tab;
import com.vaadin.ui.VerticalLayout;

/**
 * Test UI for Accordion: old widget should be removed from the tab.
 *
 * @since 7.2
 * @author Vaadin Ltd
 */
public class AccordionAddTab extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final Accordion tabs = new Accordion();
        addComponent(tabs);
        tabs.setHeight(500, Unit.PIXELS);
        Button remove = new Button("Remove 'First'");
        final Tab me = tabs.addTab(addTab("First"));
        remove.addClickListener(event -> {
            tabs.removeTab(me);
            Tab tab = tabs.addTab(addTab("Next"));
            tabs.setSelectedTab(tab);
        });
        addComponent(remove);
    }

    private Component addTab(String tag) {
        VerticalLayout layout = new VerticalLayout();
        layout.addComponent(new Label("On tab: " + tag));
        return new Panel(tag, layout);
    }

    @Override
    protected String getTestDescription() {
        return "Remove previous widget in the accordion tab when content is replaced";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11367;
    }

}
