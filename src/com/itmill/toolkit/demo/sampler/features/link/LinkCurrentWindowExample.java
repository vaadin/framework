package com.itmill.toolkit.demo.sampler.features.link;

import com.itmill.toolkit.terminal.ExternalResource;
import com.itmill.toolkit.terminal.ThemeResource;
import com.itmill.toolkit.ui.Link;
import com.itmill.toolkit.ui.OrderedLayout;

public class LinkCurrentWindowExample extends OrderedLayout {

    private static final String CAPTION = "Open Google";
    private static final String TOOLTIP = "http://www.google.com";
    private static final ThemeResource ICON = new ThemeResource(
            "icons/icon_world.gif");

    public LinkCurrentWindowExample() {
        setSpacing(true);

        // Link w/ text and tooltip
        Link l = new Link(CAPTION,
                new ExternalResource("http://www.google.com"));
        l.setDescription(TOOLTIP);
        addComponent(l);

        // Link w/ text, icon and tooltip
        l = new Link(CAPTION, new ExternalResource("http://www.google.com"));
        l.setDescription(TOOLTIP);
        l.setIcon(ICON);
        addComponent(l);

        // Link w/ icon and tooltip
        l = new Link();
        l.setResource(new ExternalResource("http://www.google.com"));
        l.setDescription(TOOLTIP);
        l.setIcon(ICON);
        addComponent(l);

    }
}
