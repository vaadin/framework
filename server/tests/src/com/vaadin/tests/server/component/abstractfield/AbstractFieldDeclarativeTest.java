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
package com.vaadin.tests.server.component.abstractfield;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.TextField;

/**
 * Tests declarative support for implementations of {@link AbstractField}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class AbstractFieldDeclarativeTest extends
        DeclarativeTestBase<AbstractField<?>> {

    @Test
    public void testPlainTextRead() {
        testRead(getDesign(), getExpected());
    }

    @Test
    public void testPlainTextWrite() {
        testWrite(getDesign(), getExpected());
    }

    protected String getDesign() {
        return "<v-text-field buffered='true' validation-visible='false' invalid-committed='true'"
                + " invalid-allowed='false' required='true' required-error='This is a required field'"
                + " conversion-error='Input {0} cannot be parsed' tabindex=3 readonly='true'/>";
    }

    protected AbstractField getExpected() {
        TextField tf = new TextField();
        tf.setBuffered(true);
        tf.setValidationVisible(false);
        tf.setInvalidCommitted(true);
        tf.setInvalidAllowed(false);
        tf.setRequired(true);
        tf.setRequiredError("This is a required field");
        tf.setConversionError("Input {0} cannot be parsed");
        tf.setTabIndex(3);
        tf.setReadOnly(true);
        return tf;
    };

}
