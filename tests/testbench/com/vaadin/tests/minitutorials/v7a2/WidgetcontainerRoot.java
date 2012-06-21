package com.vaadin.tests.minitutorials.v7a2;

import java.util.Random;

import com.vaadin.annotations.Widgetset;
import com.vaadin.terminal.WrappedRequest;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CheckBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.Label;
import com.vaadin.ui.Root;

@Widgetset("com.vaadin.tests.widgetset.TestingWidgetSet")
public class WidgetcontainerRoot extends Root {
    @Override
    public void init(WrappedRequest request) {
        Label label = new Label("Hello Vaadin user");
        addComponent(label);
        final WidgetContainer widgetContainer = new WidgetContainer();
        addComponent(widgetContainer);
        widgetContainer.addComponent(new Label(
                "Click the button to add components to the WidgetContainer."));
        Button button = new Button("Add more components", new ClickListener() {

            public void buttonClick(ClickEvent event) {
                Random randomGenerator = new Random();
                int random = randomGenerator.nextInt(3);
                Component component;
                if (random % 3 == 0) {
                    component = new Label("A new label");
                } else if (random % 3 == 1) {
                    component = new Button("A button!");
                } else {
                    component = new CheckBox("A textfield");
                }
                widgetContainer.addComponent(component);
            }
        });
        addComponent(button);
    }

}