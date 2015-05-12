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
package com.vaadin.tests.server.component.label;

import org.junit.Test;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Label;

/**
 * Tests declarative support for implementations of {@link Label}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class LabelDeclarativeTest extends DeclarativeTestBase<Label> {

    @Test
    public void testEmpty() {
        String design = "<v-label />";
        Label l = new Label();
        l.setContentMode(ContentMode.HTML);
        testRead(design, l);
        testWrite(design, l);
    }

    @Test
    public void testDefault() {
        String design = "<v-label>Hello world!</v-label>";
        Label l = createLabel("Hello world!", null, true);
        testRead(design, l);
        testWrite(design, l);
    }

    @Test
    public void testRich() {
        String design = "<v-label>This is <b><u>Rich</u></b> content!</v-label>";
        Label l = createLabel("This is \n<b><u>Rich</u></b> content!", null,
                true);
        testRead(design, l);
        testWrite(design, l);
    }

    @Test
    public void testPlainText() {
        String design = "<v-label plain-text>This is only <b>text</b>"
                + " and will contain visible tags</v-label>";
        Label l = createLabel(
                "This is only \n<b>text</b> and will contain visible tags",
                null, false);
        testRead(design, l);
        testWrite(design, l);
    }

    @Test
    public void testContentAndCaption() {
        String design = "<v-label caption='This is a label'>This is <b><u>Rich</u></b> "
                + "content!</v-label>";
        Label l = createLabel("This is \n<b><u>Rich</u></b> content!",
                "This is a label", true);
        testRead(design, l);
        testWrite(design, l);
    }

    @Test
    public void testCaption() {
        String design = "<v-label caption='This is a label' />";
        Label l = createLabel(null, "This is a label", true);
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
            label.setCaption(caption);
        }
        return label;
    }
}
