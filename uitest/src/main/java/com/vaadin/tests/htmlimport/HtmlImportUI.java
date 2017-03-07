/*
 * Copyright 2000-2016 Vaadin Ltd.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
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
