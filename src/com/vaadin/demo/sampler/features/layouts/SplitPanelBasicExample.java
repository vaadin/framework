package com.vaadin.demo.sampler.features.layouts;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.SplitPanel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class SplitPanelBasicExample extends VerticalLayout {

    public static final String brownFox = "The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. The quick brown fox jumps over the lazy dog. ";

    public SplitPanelBasicExample() {
        // First a vertical SplitPanel
        final SplitPanel vert = new SplitPanel();
        vert.setHeight("450px");
        vert.setWidth("100%");
        // vert.setOrientation(SplitPanel.ORIENTATION_VERTICAL); // default
        vert.setSplitPosition(150, SplitPanel.UNITS_PIXELS);
        addComponent(vert);

        // add a label to the upper area
        vert.addComponent(new Label(brownFox));

        // Add a horizontal SplitPanel to the lower area
        final SplitPanel horiz = new SplitPanel();
        horiz.setOrientation(SplitPanel.ORIENTATION_HORIZONTAL);
        horiz.setSplitPosition(50); // percent
        vert.addComponent(horiz);

        // left component:
        horiz.addComponent(new Label(brownFox));

        // right component:
        horiz.addComponent(new Label(brownFox));

        // Lock toggle button
        Button toggleLocked = new Button("Splits locked",
                new Button.ClickListener() {
                    // inline click.listener
                    public void buttonClick(ClickEvent event) {
                        vert.setLocked(event.getButton().booleanValue());
                        horiz.setLocked(event.getButton().booleanValue());
                    }
                });
        toggleLocked.setSwitchMode(true);
        addComponent(toggleLocked);

    }
}