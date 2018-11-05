package com.vaadin.tests.components.splitpanel;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalSplitPanel;

public class SplitPanelMoveComponent extends AbstractReindeerTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        final HorizontalSplitPanel split = new HorizontalSplitPanel();
        split.setHeight("200px");
        final Button targetComponent = new Button(
                "Button in splitpanel. Click to move to the other side");
        split.setFirstComponent(targetComponent);

        targetComponent.addClickListener(event -> {
            if (split.getFirstComponent() != null) {
                split.setFirstComponent(null);
                split.setSecondComponent(targetComponent);
            } else {
                split.setSecondComponent(null);
                split.setFirstComponent(targetComponent);
            }
        });

        addComponent(split);
    }

    @Override
    protected String getTestDescription() {
        return "Fail to swap components in HorizontalSplitPanel";
    }

    @Override
    protected Integer getTicketNumber() {
        return 11920;
    }

}
