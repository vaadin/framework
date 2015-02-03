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
package com.vaadin.tests.server.component.textfield;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.TextField;

/**
 * Tests declarative support for implementations of {@link TextField}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class TextFieldDeclarativeTest extends DeclarativeTestBase<TextField> {

    @Test
    public void testPlainTextRead() {
        testRead(getDesign(), getExpected());
    }

    @Test
    public void testPlainTextWrite() {
        testWrite(getDesign(), getExpected());
    }

    protected String getDesign() {
        return "<v-text-field/>";
    }

    protected TextField getExpected() {
        TextField tf = new TextField();
        return tf;
    };

}
