package com.vaadin.tests.components.javascriptcomponent;

import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUIWithLog;
import com.vaadin.ui.Button;
import com.vaadin.ui.Label;

@JavaScript({ "notfound.js", "notfound.js" })
@Widgetset("com.vaadin.DefaultWidgetSet")
public class DuplicateJavascriptDependencies extends AbstractTestUIWithLog {

    @JavaScript({ "notfound2.js", "notfound2.js" })
    public static class ResultLabel extends Label {

        public ResultLabel(String text) {
            super(text);
            setId("result");
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new Button("Test",
                event -> addComponent(new ResultLabel("It works"))));
    }

}
