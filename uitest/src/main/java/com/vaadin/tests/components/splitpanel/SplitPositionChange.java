package com.vaadin.tests.components.splitpanel;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.AbstractSplitPanel;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

/**
 * Test for {@link SplitPositionChangeListeners}.
 *
 * @author Vaadin Ltd
 */
public class SplitPositionChange extends AbstractTestUIWithLog {

    @Override
    protected void setup(VaadinRequest request) {
        addSplitPanel(true, "Left", "Right");
        addSplitPanel(false, "Top", "Bottom");
    }

    private void addSplitPanel(final boolean horizontal, String firstCaption,
            String secondCaption) {
        AbstractSplitPanel splitPanel;
        if (horizontal) {
            splitPanel = new HorizontalSplitPanel();
        } else {
            splitPanel = new VerticalSplitPanel();
        }
        splitPanel.setWidth("200px");
        splitPanel.setHeight("200px");
        splitPanel.addComponent(buildPanel(firstCaption));
        splitPanel.addComponent(buildPanel(secondCaption));
        splitPanel.addSplitPositionChangeListener(
                event -> log(String.format(
                        "Split position changed: %s, position: %s %s",
                        (horizontal ? "horizontal" : "vertical"),
                        event.getSplitPosition(),
                        event.getSplitPositionUnit())));
        addComponent(splitPanel);
    }

    private Panel buildPanel(String caption) {
        VerticalLayout pl = new VerticalLayout();
        pl.setMargin(true);
        pl.addComponent(new Label("content"));
        Panel panel = new Panel(caption, pl);
        panel.setSizeFull();
        return panel;
    }

    @Override
    protected String getTestDescription() {
        return "SplitPanel should have an event for the splitter being moved";
    }

    @Override
    protected Integer getTicketNumber() {
        return 3855;
    }

}
