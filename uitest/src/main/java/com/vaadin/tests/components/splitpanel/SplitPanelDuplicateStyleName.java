package com.vaadin.tests.components.splitpanel;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.AbstractSplitPanel;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalSplitPanel;

/**
 * Test UI for duplicate primary style name in SplitPanel.
 *
 * @author Vaadin Ltd
 */
public class SplitPanelDuplicateStyleName extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        addSplitPanel(true);
        addSplitPanel(false);
    }

    private void addSplitPanel(final boolean horizontal) {
        AbstractSplitPanel splitPanel;
        if (horizontal) {
            splitPanel = new HorizontalSplitPanel();
        } else {
            splitPanel = new VerticalSplitPanel();
        }
        splitPanel.setWidth("200px");
        splitPanel.setHeight("200px");
        addComponent(splitPanel);
    }

    @Override
    protected String getTestDescription() {
        return "SplitPanel should not have duplicate primary style name";
    }

    @Override
    protected Integer getTicketNumber() {
        return 17846;
    }

}
