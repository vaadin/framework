package com.vaadin.tests.components.tabsheet;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.VerticalLayout;

/**
 * If the space is pressed on the tabs of a tabsheet the browser default scroll
 * behavior must be prevented.
 *
 * @author Vaadin Ltd
 */
public class TabSpaceNotScroll extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        TabSheet tabSheet = new TabSheet();

        for (int i = 0; i < 5; i++) {
            String caption = "Tab " + i;
            Component c = new Label(caption);
            tabSheet.addTab(c, caption);
        }

        addComponent(tabSheet);

        Label dontShowThis = new Label("Page scroll. This is bad.");

        VerticalLayout panel = new VerticalLayout();
        panel.setHeight("2000px");
        panel.addComponent(dontShowThis);
        panel.setComponentAlignment(dontShowThis, Alignment.MIDDLE_CENTER);

        addComponent(panel);
    }

    @Override
    protected String getTestDescription() {
        return "Pressing space on the tab should not scroll.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14320;
    }

}
