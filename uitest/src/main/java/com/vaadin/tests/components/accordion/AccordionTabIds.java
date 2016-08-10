package com.vaadin.tests.components.accordion;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Accordion;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet.Tab;

@SuppressWarnings("serial")
public class AccordionTabIds extends AbstractTestUI {

    protected static final String FIRST_TAB_ID = "ID 1";
    protected static final String FIRST_TAB_MESSAGE = "First tab";

    /*
     * (non-Javadoc)
     *
     * @see com.vaadin.tests.components.AbstractTestUI#setup(com.vaadin.server.
     * VaadinRequest)
     */
    @Override
    protected void setup(VaadinRequest request) {
        Accordion accordion = new Accordion();
        final Tab firstTab = accordion.addTab(new Label(FIRST_TAB_MESSAGE));
        firstTab.setId(FIRST_TAB_ID);
        Button setIdButton = new Button("Set id");
        setIdButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                firstTab.setId(FIRST_TAB_ID);
            }
        });
        Button clearIdButton = new Button("Clear id");
        clearIdButton.addClickListener(new ClickListener() {

            @Override
            public void buttonClick(ClickEvent event) {
                firstTab.setId(null);
            }
        });
        addComponents(setIdButton, clearIdButton, accordion);
    }

    @Override
    protected String getTestDescription() {
        return "Accordion should set server side defined ids on Tabs.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 18456;
    }
}
