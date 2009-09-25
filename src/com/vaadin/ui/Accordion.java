package com.vaadin.ui;

import com.vaadin.terminal.gwt.client.ui.VAccordion;

@SuppressWarnings("serial")
@ClientWidget(VAccordion.class)
public class Accordion extends TabSheet {

    @Override
    public String getTag() {
        return "accordion";
    }

}
