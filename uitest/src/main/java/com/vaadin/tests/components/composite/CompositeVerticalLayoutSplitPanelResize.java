package com.vaadin.tests.components.composite;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Composite;
import com.vaadin.ui.HorizontalSplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.VerticalSplitPanel;

public class CompositeVerticalLayoutSplitPanelResize extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new CompositeVSP());

        getLayout().setSizeFull();
        getLayout().getParent().setSizeFull();
    }

    public class CompositeVSP extends Composite {
        public CompositeVSP() {
            VerticalSplitPanel verticalSplitPanel = new VerticalSplitPanel();
            verticalSplitPanel.setSecondComponent(new CompositeHSP());

            VerticalLayout root = new VerticalLayout();
            root.setId("root");
            root.setMargin(false);
            root.addComponent(verticalSplitPanel);

            setCompositionRoot(root);
            setSizeFull();
        }
    }

    public class CompositeHSP extends Composite {
        public CompositeHSP() {
            HorizontalSplitPanel horizontalSplitPanel = new HorizontalSplitPanel();

            VerticalLayout root = new VerticalLayout();
            root.setSizeFull();
            root.setMargin(false);
            root.addComponent(horizontalSplitPanel);

            setCompositionRoot(root);
            setSizeFull();
        }
    }

    @Override
    protected String getTestDescription() {
        return "Composite contents should resize without a delay when the"
                + " browser is resized, not only when interacted with.";
    }

    @Override
    protected Integer getTicketNumber() {
        return 12153;
    }
}
