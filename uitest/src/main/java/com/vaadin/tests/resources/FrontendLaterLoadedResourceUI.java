package com.vaadin.tests.resources;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class FrontendLaterLoadedResourceUI extends AbstractTestUIWithLog {

    @JavaScript("frontend://logFilename.js")
    public static class MyButton extends Button {

    }

    @Override
    protected void setup(VaadinRequest request) {
        Button b = new MyButton();
        b.addClickListener(event -> getPage().getJavaScript()
                .execute("document.body.innerHTML=window.jsFile;\n"));
        addComponent(b);
    }

}
