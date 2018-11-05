package com.vaadin.tests.components.composite;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Composite;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class CompositePanelCaptionUI extends AbstractTestUI {
    @Override
    public void setup(VaadinRequest request) {
        Panel regularPanel = new Panel("Regular ol' panel",
                createPanelContent());
        CustomComponentPanel customComponentPanel = new CustomComponentPanel();
        CompositePanel compositePanel = new CompositePanel();

        addComponents(regularPanel, customComponentPanel, compositePanel);
    }

    private VerticalLayout createPanelContent() {
        Label helloWorld = new Label("Hello world!");
        helloWorld.addStyleName(ValoTheme.LABEL_HUGE);

        return new VerticalLayout(helloWorld);
    }

    private class CustomComponentPanel extends CustomComponent {
        public CustomComponentPanel() {
            Panel panel = new Panel("CustomComponentPanel",
                    createPanelContent());
            setCompositionRoot(panel);
        }
    }

    private class CompositePanel extends Composite {
        public CompositePanel() {
            Panel panel = new Panel("CompositePanel", createPanelContent());
            setCompositionRoot(panel);
        }
    }
}
