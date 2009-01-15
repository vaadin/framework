package com.itmill.toolkit.demo.sampler.features.link;

import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.VerticalLayout;

public class LinkNoDecorationsExample extends VerticalLayout {

    private static final String CAPTION = "Open Google in new window";
    private static final String TOOLTIP = "http://www.google.com (opens in new window)";
    private static final ThemeResource ICON = new ThemeResource(
            "icons/icon_world.gif");

    public LinkNoDecorationsExample() {
        setSpacing(true);

        // Link w/ text and tooltip
        Link l = new Link(CAPTION,
                new ExternalResource("http://www.google.com"));
        l.setTargetName("_blank");
        l.setTargetBorder(Link.TARGET_BORDER_NONE);
        l.setDescription(TOOLTIP);
        addComponent(l);

        // Link w/ text, icon and tooltip
        l = new Link(CAPTION, new ExternalResource("http://www.google.com"));
        l.setTargetName("_blank");
        l.setTargetBorder(Link.TARGET_BORDER_NONE);
        l.setDescription(TOOLTIP);
        l.setIcon(ICON);
        addComponent(l);

        // Link w/ icon and tooltip
        l = new Link();
        l.setResource(new ExternalResource("http://www.google.com"));
        l.setTargetName("_blank");
        l.setTargetBorder(Link.TARGET_BORDER_NONE);
        l.setDescription(TOOLTIP);
        l.setIcon(ICON);
        addComponent(l);

    }
}
