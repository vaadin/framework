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

    @HtmlImport("label2.html")
    @JavaScript("label2.js")
    public static class Label2WithImports extends LabelWithImports {

        public Label2WithImports(String text) {
            super(text);
        }
    }

    @HtmlImport("labelX.html")
    @JavaScript("labelX.js")
    public static class LabelXWithImports extends LabelWithImports {

        public LabelXWithImports(String text) {
            super(text);
        }
    }

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new Label2WithImports("Foo"));
        addComponent(new LabelXWithImports("Foo"));
    }

}
