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
    public void testDefaultRead() {
        testRead(getDefaultDesign(), getDefaultExpected());
    }

    @Test
    public void testDefaultWrite() {
        testWrite(getDefaultDesign(), getDefaultExpected());
    }

    protected String getDefaultDesign() {
        return "<v-label>Hello world!</v-label>";
    }

    protected Label getDefaultExpected() {
        Label tf = new Label();
        tf.setContentMode(ContentMode.HTML);
        tf.setValue("Hello world!");
        return tf;
    };

    @Test
    public void testRichRead() {
        testRead(getRichDesign(), getRichExpected());
    }

    @Test
    public void testRichWrite() {
        testWrite(getRichDesign(), getRichExpected());
    }

    protected String getRichDesign() {
        return "<v-label>This is <b><u>Rich</u></b> content!</v-label>";
    }

    protected Label getRichExpected() {
        Label tf = new Label();
        tf.setContentMode(ContentMode.HTML);
        tf.setValue("This is \n<b><u>Rich</u></b> content!");
        return tf;
    };

    @Test
    public void testPlainTextRead() {
        testRead(getPlainTextDesign(), getPlainTextExpected());
    }

    @Test
    public void testPlainTextWrite() {
        testWrite(getPlainTextDesign(), getPlainTextExpected());
    }

    protected String getPlainTextDesign() {
        return "<v-label plain-text>This is only <b>text</b> and will contain visible tags</v-label>";
    }

    protected Label getPlainTextExpected() {
        Label tf = new Label();
        tf.setContentMode(ContentMode.TEXT);
        tf.setValue("This is only \n<b>text</b> and will contain visible tags");
        return tf;
    };

}
