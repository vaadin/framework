package com.vaadin.tests.components.splitpanel;

import com.vaadin.server.Sizeable;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.VerticalSplitPanel;

public class RetainSplitterPositionWhenOutOfBounds
        extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        // Replacing default content to get the intended expansions
        setContent(new MainLayout());
    }

    public class MainLayout extends GridLayout {

        public MainLayout() {
            super(1, 3);
            setSizeFull();

            VerticalSplitPanel splitPanel = new VerticalSplitPanel();
            splitPanel.setFirstComponent(new Label("Top"));
            splitPanel.setSecondComponent(new Label("Middle"));
            splitPanel.setSplitPosition(50, Sizeable.Unit.PERCENTAGE);

            HorizontalLayout bottom = new HorizontalLayout();
            bottom.setWidth("100%");
            bottom.addComponent(new Label("Bottom"));

            addComponent(new Label(getTestDescription()));
            addComponent(splitPanel);
            addComponent(bottom);
        }

    }

    @Override
    protected String getTestDescription() {
        return "The original splitter position value should be respected even if it's recalculated because it's of out bounds.";
    }

    @Override
    protected Integer getTicketNumber() {
        return Integer.valueOf(10596);
    }

}
