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
package com.vaadin.tests.server.component.fieldgroup;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.ui.TextField;

/**
 * 
 * Tests for {@link FieldGroup}.
 * 
 * @author Vaadin Ltd
 */
public class FieldGroupTest {

    @Test
    public void setReadOnly_readOnlyAndNoDataSource_fieldIsReadOnly() {
        FieldGroup fieldGroup = new FieldGroup();

        TextField field = new TextField();
        fieldGroup.bind(field, "property");

        fieldGroup.setReadOnly(true);

        Assert.assertTrue("Field is not read only", field.isReadOnly());
    }

    @Test
    public void setReadOnly_writableAndNoDataSource_fieldIsWritable() {
        FieldGroup fieldGroup = new FieldGroup();

        TextField field = new TextField();
        fieldGroup.bind(field, "property");

        fieldGroup.setReadOnly(false);

        Assert.assertFalse("Field is not writable", field.isReadOnly());
    }
}
