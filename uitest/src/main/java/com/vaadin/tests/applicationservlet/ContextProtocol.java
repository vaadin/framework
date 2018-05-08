package com.vaadin.tests.applicationservlet;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.ExternalResource;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Image;

@JavaScript("context://statictestfiles/sayHello.js")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class ContextProtocol extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Image image = new Image("Image from context root",
                new ExternalResource("context://statictestfiles/image.png"));
        image.setId("image");
        addComponent(image);
    }

}
