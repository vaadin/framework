package com.vaadin.demo.sampler.features.buttons;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

@SuppressWarnings("serial")
public class ButtonSwitchExample extends VerticalLayout implements
        Button.ClickListener {

    private static final String CAPTION = "Allow HTML";
    private static final String TOOLTIP = "Allow/disallow HTML in comments";
    private static final ThemeResource ICON = new ThemeResource(
            "icons/page_code.gif");

    public ButtonSwitchExample() {
        setSpacing(true);

        // Button w/ text and tooltip
        Button b = new Button(CAPTION);
        b.setSwitchMode(true);
        b.setDescription(TOOLTIP);
        b.addListener(this); // react to clicks
        addComponent(b);

        // Button w/ text, icon and tooltip
        b = new Button(CAPTION);
        b.setSwitchMode(true);
        b.setDescription(TOOLTIP);
        b.setIcon(ICON);
        b.addListener(this); // react to clicks
        addComponent(b);

        // Button w/ icon and tooltip
        b = new Button();
        b.setSwitchMode(true);
        b.setDescription(TOOLTIP);
        b.setIcon(ICON);
        b.addListener(this); // react to clicks
        addComponent(b);

    }

    /*
     * Shows a notification when a button is clicked.
     */
    public void buttonClick(ClickEvent event) {
        boolean enabled = event.getButton().booleanValue();
        getWindow().showNotification(
                "HTML " + (enabled ? "enabled" : "disabled"));
    }
}
