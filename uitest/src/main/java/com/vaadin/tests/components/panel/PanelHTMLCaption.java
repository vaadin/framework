package com.vaadin.tests.components.panel;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Panel;
import com.vaadin.ui.Button;
import com.vaadin.ui.VerticalLayout;

public class PanelHTMLCaption extends AbstractTestUI {
    public static final String caption = "<b> <div id='divId'>BOLD CAPTION</div></b>";

    @Override
    protected void setup(VaadinRequest request) {
        Panel panel = new Panel();
        panel.setId("panelId");
        panel.setWidth("200px");
        panel.setHeight("200px");

        panel.setCaption(caption);
        panel.setCaptionAsHtml(false);

        panel.setContent(new VerticalLayout());

        addComponent(panel);
        Button changeCaptionFormat = new Button("Set Caption as HTML", e -> {
            panel.setCaptionAsHtml(true);
        });
        changeCaptionFormat.setId("buttonId");
        addComponent(changeCaptionFormat);
    }
}