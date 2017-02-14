/*
 * Copyright 2000-2017 Vaadin Ltd.
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
package com.vaadin.v7.tests.server.component.abstractfield;

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
public class AbstractFieldDeclarativeTest
        extends DeclarativeTestBase<AbstractField<?>> {

    @Test
    public void testPlainText() {
        // FIXME
        // String design = "<vaadin-text-field readonly tabindex=3"
        // + "required"
        // + "/>";
        // AbstractField<String> tf = new TextField();
        // tf.setRequired(true);
        // tf.setTabIndex(3);
        // tf.setReadOnly(true);
        // testRead(design, tf);
        // testWrite(design, tf);
        //
        // // Test with readonly=false
        // design = design.replace("readonly", "");
        // tf.setReadOnly(false);
        // testRead(design, tf);
        // testWrite(design, tf);
    }

    @Test
    public void testModelReadOnly() {
        // Test that read only value coming from property data source is not
        // written to design.
        String design = "<vaadin-text-field readonly value=test></vaadin-text-field>";
        AbstractField<String> component = new TextField();
        component.setReadOnly(true);
        component.setValue("test");
        // FIXME (?) current implementation only
        // disables client-side modification
        testWrite(design, component);
    }
}
