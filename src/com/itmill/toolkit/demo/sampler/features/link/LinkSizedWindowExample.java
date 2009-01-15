package com.itmill.toolkit.demo.sampler.features.link;

import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.terminal.Resource;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.VerticalLayout;

public class LinkSizedWindowExample extends VerticalLayout {

    private static final String CAPTION = "Open Google in small window";
    private static final String TOOLTIP = "http://www.google.com (opens in small window)";
    private static final ThemeResource ICON = new ThemeResource(
            "icons/icon_world.gif");
    private static final Resource TARGET = new ExternalResource(
            "http://www.google.com/m");

    public LinkSizedWindowExample() {
        setSpacing(true);

        // Link w/ text and tooltip
        Link l = new Link(CAPTION, TARGET);
        l.setTargetName("_blank");
        l.setTargetWidth(300);
        l.setTargetHeight(300);
        l.setTargetBorder(Link.TARGET_BORDER_NONE);
        l.setDescription(TOOLTIP);
        addComponent(l);

        // Link w/ text, icon and tooltip
        l = new Link(CAPTION, TARGET);
        l.setTargetName("_blank");
        l.setTargetWidth(300);
        l.setTargetHeight(300);
        l.setTargetBorder(Link.TARGET_BORDER_NONE);
        l.setDescription(TOOLTIP);
        l.setIcon(ICON);
        addComponent(l);

        // Link w/ icon and tooltip
        l = new Link();
        l.setResource(TARGET);
        l.setTargetName("_blank");
        l.setTargetWidth(300);
        l.setTargetHeight(300);
        l.setTargetBorder(Link.TARGET_BORDER_NONE);
        l.setDescription(TOOLTIP);
        l.setIcon(ICON);
        addComponent(l);

    }
}
