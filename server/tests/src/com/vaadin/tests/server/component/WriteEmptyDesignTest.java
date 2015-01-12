/*
 * Copyright 2000-2014 Vaadin Ltd.
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
package com.vaadin.tests.server.component;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;

import junit.framework.TestCase;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import com.vaadin.ui.Component;
import com.vaadin.ui.declarative.Design;
import com.vaadin.ui.declarative.DesignContext;

/**
 * Test cases for checking that writing a component hierarchy with null root
 * produces an html document that has no elements in the html body.
 */
public class WriteEmptyDesignTest extends TestCase {

    public void testWriteComponent() throws IOException {
        OutputStream os = new ByteArrayOutputStream();
        Design.write((Component) null, os);
        checkHtml(os.toString());
    }

    public void testWriteContext() throws IOException {
        OutputStream os = new ByteArrayOutputStream();
        DesignContext ctx = new DesignContext();
        ctx.setRootComponent(null);
        Design.write(ctx, os);
        checkHtml(os.toString());
    }

    private void checkHtml(String html) {
        Document doc = Jsoup.parse(html);
        Element body = doc.body();
        assertEquals("There should be no elements in the html body.", "",
                body.html());
    }
}