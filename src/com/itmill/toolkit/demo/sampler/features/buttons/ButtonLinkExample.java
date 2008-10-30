package com.itmill.toolkit.demo.sampler.features.buttons;

import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.ui.Button;
import com.itmill.toolkit.ui.OrderedLayout;
import com.itmill.toolkit.ui.Button.ClickEvent;

public class ButtonLinkExample extends OrderedLayout implements
        Button.ClickListener {

    private static final String CAPTION = "Help";
    private static final String TOOLTIP = "Show help";
    private static final ThemeResource ICON = new ThemeResource(
            "icons/icon_info.gif");
    private static final String NOTIFICATION = "Help clicked";

    public ButtonLinkExample() {
        setSpacing(true);

        // Button w/ text and tooltip
        Button b = new Button(CAPTION);
        b.setStyleName(Button.STYLE_LINK);
        b.setDescription(TOOLTIP);
        b.addListener(this); // react to clicks
        addComponent(b);

        // Button w/ text, icon and tooltip
        b = new Button(CAPTION);
        b.setStyleName(Button.STYLE_LINK);
        b.setDescription(TOOLTIP);
        b.setIcon(ICON);
        b.addListener(this); // react to clicks
        addComponent(b);

        // Button w/ icon and tooltip
        b = new Button();
        b.setStyleName(Button.STYLE_LINK);
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
