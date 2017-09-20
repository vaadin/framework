package com.vaadin.tests.componentlocator;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.Label;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;

@Widgetset("com.vaadin.DefaultWidgetSet")
public class ComponentLocatorInheritedClasses extends UI {

    public static class DefaultLabel extends Label {

        protected DefaultLabel(String content) {
            super(content);
        }

        public DefaultLabel() {
            this("Default Custom Label");
        }
    }

    public static class MyCustomLabel extends DefaultLabel {
        public MyCustomLabel(String content) {
            super(content);
        }
    }

    @Override
    protected void init(VaadinRequest request) {
        VerticalLayout layout = new VerticalLayout();
        layout.addComponents(new Label("Vaadin Basic Label"),
                new DefaultLabel(), new MyCustomLabel("My Custom Label"));
        setContent(layout);
    }
}
