package com.vaadin.tests.htmlimport;

import com.vaadin.annotations.HtmlImport;
import com.vaadin.annotations.JavaScript;
import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Label;

@JavaScript("webcomponents-lite.min.js")
@JavaScript("ui.js")
@HtmlImport("ui.html")
@Widgetset("com.vaadin.DefaultWidgetSet")
public class HtmlImportUI extends AbstractTestUI {

    @HtmlImport("label.html")
    @JavaScript("label.js")
    public static class LabelWithImports extends Label {

        public LabelWithImports(String text) {
            super(text);
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new LabelWithImports("Foo"));
    }

}
