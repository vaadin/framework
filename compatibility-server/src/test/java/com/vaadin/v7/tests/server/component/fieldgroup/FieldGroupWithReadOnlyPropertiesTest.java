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
package com.vaadin.v7.tests.server.component.fieldgroup;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

import com.vaadin.tests.data.bean.BeanWithReadOnlyField;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.util.BeanItem;
import com.vaadin.v7.ui.TextField;

public class FieldGroupWithReadOnlyPropertiesTest {

    private TextField readOnlyField = new TextField();
    private TextField writableField = new TextField();

    @Test
    public void bindReadOnlyPropertyToFieldGroup() {
        BeanWithReadOnlyField bean = new BeanWithReadOnlyField();
        BeanItem<BeanWithReadOnlyField> beanItem = new BeanItem<BeanWithReadOnlyField>(
                bean);
        beanItem.getItemProperty("readOnlyField").setReadOnly(true);

        FieldGroup fieldGroup = new FieldGroup(beanItem);
        fieldGroup.bindMemberFields(this);

        assertTrue(readOnlyField.isReadOnly());
        assertFalse(writableField.isReadOnly());
    }

    @Test
    public void fieldGroupSetReadOnlyTest() {
        BeanWithReadOnlyField bean = new BeanWithReadOnlyField();
        BeanItem<BeanWithReadOnlyField> beanItem = new BeanItem<BeanWithReadOnlyField>(
                bean);
        beanItem.getItemProperty("readOnlyField").setReadOnly(true);

        FieldGroup fieldGroup = new FieldGroup(beanItem);
        fieldGroup.bindMemberFields(this);

        fieldGroup.setReadOnly(true);
        assertTrue(readOnlyField.isReadOnly());
        assertTrue(writableField.isReadOnly());

        fieldGroup.setReadOnly(false);
        assertTrue(readOnlyField.isReadOnly());
        assertFalse(writableField.isReadOnly());
    }

}
