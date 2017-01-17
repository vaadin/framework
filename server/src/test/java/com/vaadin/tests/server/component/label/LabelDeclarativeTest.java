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
package com.vaadin.tests.server.component.label;

import org.jsoup.nodes.Element;
import org.jsoup.parser.Tag;
import org.junit.Assert;
import org.junit.Ignore;
import org.junit.Test;

import com.vaadin.shared.ui.ContentMode;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Label;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.ui.declarative.DesignFormatter;

/**
 * Tests declarative support for implementations of {@link Label}.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class LabelDeclarativeTest extends DeclarativeTestBase<Label> {

    @Test
    public void testEmpty() {
        String design = "<vaadin-label />";
        Label l = new Label();
        l.setContentMode(ContentMode.HTML);
        testRead(design, l);
        testWrite(design, l);
    }

    @Test
    public void testDefault() {
        String design = "<vaadin-label>Hello world!</vaadin-label>";
        Label l = createLabel("Hello world!", null, true);
        testRead(design, l);
        testWrite(design, l);
    }

    @Test
    public void testRich() {
        String design = "<vaadin-label>This is <b><u>Rich</u></b> content!</vaadin-label>";
        Label l = createLabel("This is \n<b><u>Rich</u></b> content!", null,
                true);
        testRead(design, l);
        testWrite(design, l);
    }

    @Test
    public void testPlainText() {
        String design = "<vaadin-label plain-text>This is only &lt;b&gt;text&lt;/b&gt;"
                + " and will contain visible tags</vaadin-label>";
        Label l = createLabel(
                "This is only <b>text</b> and will contain visible tags", null,
                false);
        testRead(design, l);
        testWrite(design, l);
    }

    @Test
    public void testContentAndCaption() {
        String design = "<vaadin-label caption='This is a label'>This is <b><u>Rich</u></b> "
                + "content!</vaadin-label>";
        Label l = createLabel("This is \n<b><u>Rich</u></b> content!",
                "This is a label", true);
        testRead(design, l);
        testWrite(design, l);
    }

    @Test
    public void testCaption() {
        String design = "<vaadin-label caption='This is a label' />";
        Label l = createLabel(null, "This is a label", true);
        testRead(design, l);
        testWrite(design, l);
    }

    @Test
    public void testHtmlEntities() {
        String design = "<vaadin-label plain-text=\"true\">&gt; Test</vaadin-label>";
        Label read = read(design);
        Assert.assertEquals("> Test", read.getValue());

        design = design.replace("plain-text=\"true\"", "");
        read = read(design);
        Assert.assertEquals("&gt; Test", read.getValue());

        Label label = new Label("&amp; Test");
        label.setContentMode(ContentMode.TEXT);

        Element root = new Element(Tag.valueOf("vaadin-label"), "");
        label.writeDesign(root, new DesignContext());
        Assert.assertEquals("&amp;amp; Test", root.html());

        label.setContentMode(ContentMode.HTML);
        root = new Element(Tag.valueOf("vaadin-label"), "");
        label.writeDesign(root, new DesignContext());
        Assert.assertEquals("&amp; Test", root.html());
    }

    /**
     * FIXME Using another content mode than TEXT OR HTML is currently not
     * supported and will cause the content mode to fallback without the users
     * knowledge to HTML. This test can be enabled when
     * https://dev.vaadin.com/ticket/19435 is fixed.
     */
    @Test
    @Ignore("Test ignored due to https://dev.vaadin.com/ticket/19435")
    public void testContentModes() {
        String design = "<vaadin-label caption='This\n is a label' />";
        Label l = createLabel(null, "This\n is a label", true);
        l.setContentMode(ContentMode.PREFORMATTED);
        testRead(design, l);
        testWrite(design, l);
    }

    private Label createLabel(String content, String caption, boolean html) {
        Label label = new Label();
        label.setContentMode(html ? ContentMode.HTML : ContentMode.TEXT);
        if (content != null) {
            label.setValue(content);
        }
        if (caption != null) {
            label.setCaption(DesignFormatter.encodeForTextNode(caption));
        }
        return label;
    }
}
