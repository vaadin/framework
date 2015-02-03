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
package com.vaadin.tests.server.component.button;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Button;

/**
 * Tests declarative support for implementations of {@link Button}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class ButtonDeclarativeTest extends DeclarativeTestBase<Button> {

    @Test
    public void testPlainTextRead() {
        testRead(getDesignPlainText(), getExpectedPlainText());
    }

    @Test
    public void testPlainTextWrite() {
        testWrite(getDesignPlainText(), getExpectedPlainText());
    }

    protected String getDesignPlainText() {
        return "<v-button plain-text=''></v-button>";
    }

    protected Button getExpectedPlainText() {
        Button c = new Button();
        c.setCaption("");
        return c;
    };

    @Test
    public void testHtmlRead() {
        testRead(getDesignHtml(), getExpectedHtml());
    }

    @Test
    public void testHtmlWrite() {
        testWrite(getDesignHtml(), getExpectedHtml());
    }

    protected String getDesignHtml() {
        return "<v-button />";
    }

    protected Button getExpectedHtml() {
        Button c = new Button();
        c.setCaption("");
        c.setCaptionAsHtml(true);
        return c;
    };

}
