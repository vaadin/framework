package com.vaadin.tests.components.image;

import com.vaadin.server.ThemeResource;
import com.vaadin.tests.components.TestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Image;

public class ImageAltText extends TestBase {

    @Override
    protected void setup() {
        final Image image = new Image("Caption", new ThemeResource(
                "../runo/icons/64/ok.png"));
        image.setDebugId("image");
        image.setAlternateText("Original alt text");
        addComponent(image);

        Button changeAltTexts = new Button("Change alt text",
                new Button.ClickListener() {
                    @Override
                    public void buttonClick(ClickEvent event) {
                        image.setAlternateText("New alt text!");
                    }
                });
        addComponent(changeAltTexts);
    }

    @Override
    protected String getDescription() {
        return "Test alternative text of image";
    }

    @Override
    protected Integer getTicketNumber() {
        return null;
    }

}
