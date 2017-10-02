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
package com.vaadin.tests.design;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.junit.Test;

import com.vaadin.annotations.DesignRoot;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;

public class FieldNameWhichConflictsWithGettersTest {

    @DesignRoot("MyVerticalLayout.html")
    public static class MyVerticalLayout extends VerticalLayout {
        private Label caption;
        private TextField description;

        public MyVerticalLayout() {
            Design.read(this);
        }
    }

    @Test
    public void readWithConflictingFields() {
        MyVerticalLayout v = new MyVerticalLayout();
        assertNotNull(v.caption);
        assertNotNull(v.description);
    }

    @Test
    public void writeWithConflictingFields() throws IOException {
        VerticalLayout v = new VerticalLayout();
        Label l = new Label();
        l.setId("caption");
        TextField tf = new TextField();
        v.addComponents(l, tf);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();

        DesignContext context = new DesignContext();
        context.setComponentLocalId(tf, "description");
        context.setRootComponent(v);

        Design.write(context, baos);
        String str = baos.toString(UTF_8.name());

        Document doc = Jsoup.parse(str);
        Element body = doc.body();
        Element captionElement = body.getElementById("caption");
        assertNotNull(captionElement);
        assertEquals("vaadin-label", captionElement.tagName());

        Element descriptionElement = captionElement.nextElementSibling();
        assertNotNull(descriptionElement);
        assertEquals("vaadin-text-field", descriptionElement.tagName());
        assertEquals("description", descriptionElement.attr("_id"));

    }
}
