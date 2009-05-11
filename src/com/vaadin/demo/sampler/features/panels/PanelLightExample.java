package com.vaadin.demo.sampler.features.panels;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;

public class PanelLightExample extends VerticalLayout implements ClickListener {

    private Panel panel;

    public PanelLightExample() {
        setSpacing(true);

        setSpacing(true);

        // Panel 1 - with caption
        panel = new Panel("This is a light Panel");
        panel.setStyleName(Panel.STYLE_LIGHT);
        panel.setHeight("200px"); // we want scrollbars

        // let's adjust the panels default layout (a VerticalLayout)
        VerticalLayout layout = (VerticalLayout) panel.getLayout();
        layout.setMargin(true); // we want a margin
        layout.setSpacing(true); // and spacing between components
        addComponent(panel);

        // Let's add a few rows to provoke scrollbars:
        for (int i = 0; i < 20; i++) {
            panel.addComponent(new Label(
                    "The quick brown fox jumps over the lazy dog."));
        }

        // Caption toggle:
        Button b = new Button("Toggle caption");
        b.addListener(this);
        addComponent(b);
    }

    public void buttonClick(ClickEvent event) {
        if (panel.getCaption() == null) {
            panel.setCaption("This is a light Panel");
        } else {
            panel.setCaption(null);
        }
    }
}
