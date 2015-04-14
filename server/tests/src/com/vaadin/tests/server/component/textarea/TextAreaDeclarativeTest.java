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
package com.vaadin.tests.server.component.textarea;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.TextArea;

/**
 * Tests declarative support for implementations of {@link TextArea}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class TextAreaDeclarativeTest extends DeclarativeTestBase<TextArea> {

    @Test
    public void testTextArea() {
        String design = "<v-text-area rows=6 wordwrap=false>Hello World!</v-text-area>";
        TextArea ta = new TextArea();
        ta.setRows(6);
        ta.setWordwrap(false);
        ta.setValue("Hello World!");
        testRead(design, ta);
        testWrite(design, ta);
    }
}
