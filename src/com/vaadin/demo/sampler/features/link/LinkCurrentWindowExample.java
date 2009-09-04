package com.vaadin.demo.sampler.features.link;

import com.vaadin.terminal.ExternalResource;
import com.vaadin.terminal.ThemeResource;
import com.vaadin.ui.Link;
import com.vaadin.ui.VerticalLayout;

@SuppressWarnings("serial")
public class LinkCurrentWindowExample extends VerticalLayout {

    private static final String CAPTION = "Open Google";
    private static final String TOOLTIP = "http://www.google.com";
    private static final ThemeResource ICON = new ThemeResource("../sampler/icons/icon_world.gif");

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
