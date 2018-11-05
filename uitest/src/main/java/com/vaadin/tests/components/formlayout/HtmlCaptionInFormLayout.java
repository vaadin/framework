package com.vaadin.tests.components.formlayout;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractReindeerTestUI;
import com.vaadin.ui.Button;
import com.vaadin.ui.FormLayout;
import com.vaadin.v7.ui.TextField;

public class HtmlCaptionInFormLayout extends AbstractReindeerTestUI {
    @Override
    protected void setup(VaadinRequest request) {
        final TextField asHtml = new TextField("Contains <b>HTML</b>");
        asHtml.setCaptionAsHtml(true);

        final TextField asText = new TextField("Contains <b>HTML</b>");

        addComponent(new FormLayout(asHtml, asText));

        addComponent(new Button("Toggle", event -> {
            asHtml.setCaptionAsHtml(!asHtml.isCaptionAsHtml());
            asText.setCaptionAsHtml(!asText.isCaptionAsHtml());
        }));
    }
}
