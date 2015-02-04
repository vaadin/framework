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
package com.vaadin.tests.server.component.richtextarea;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.RichTextArea;

public class RichTextAreaDeclarativeTest extends
        DeclarativeTestBase<RichTextArea> {

    private String getBasicDesign() {
        return "<v-rich-text-area null-representation='' null-setting-allowed='true'>\n"
                + "\n      <b>Header</b> <br/>Some text\n      "
                + "</v-rich-text-area>";
    }

    private RichTextArea getBasicExpected() {
        RichTextArea rta = new RichTextArea();
        rta.setNullRepresentation("");
        rta.setNullSettingAllowed(true);
        rta.setValue("<b>Header</b> \n<br>Some text");
        return rta;
    }

    @Test
    public void testBasicRead() {
        testRead(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void testBasicWrite() {
        testWrite(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void testReadEmpty() {
        testRead("<v-rich-text-area />", new RichTextArea());
    }

    @Test
    public void testWriteEmpty() {
        testWrite("<v-rich-text-area />", new RichTextArea());
    }
}
