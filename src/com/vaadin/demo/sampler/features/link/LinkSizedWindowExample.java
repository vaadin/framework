package com.vaadin.demo.sampler.features.link;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.Resource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class LinkSizedWindowExample extends VerticalLayout {

    private static final String CAPTION = "Open Google in small window";
    private static final String TOOLTIP = "http://www.google.com (opens in small window)";
    private static final ThemeResource ICON = new ThemeResource("../sampler/icons/icon_world.gif");
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
