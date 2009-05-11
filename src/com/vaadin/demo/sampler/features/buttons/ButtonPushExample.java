package com.vaadin.demo.sampler.features.buttons;

import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Button.ClickEvent;

public class ButtonPushExample extends VerticalLayout implements
        Button.ClickListener {

    private static final String CAPTION = "Save";
    private static final String TOOLTIP = "Save changes";
    private static final ThemeResource ICON = new ThemeResource(
            "icons/action_save.gif");
    private static final String NOTIFICATION = "Changes have been saved";

    public ButtonPushExample() {
        setSpacing(true);

        // Button w/ text and tooltip
        Button b = new Button(CAPTION);
        b.setDescription(TOOLTIP);
        b.addListener(this); // react to clicks
        addComponent(b);

        // Button w/ text, icon and tooltip
        b = new Button(CAPTION);
        b.setDescription(TOOLTIP);
        b.setIcon(ICON);
        b.addListener(this); // react to clicks
        addComponent(b);

        // Button w/ icon and tooltip
        b = new Button();
        b.setDescription(TOOLTIP);
        b.setIcon(ICON);
        b.addListener(this); // react to clicks
        addComponent(b);

    }

    /*
     * Shows a notification when a button is clicked.
     */
    public void buttonClick(ClickEvent event) {
        getWindow().showNotification(NOTIFICATION);
    }
}
