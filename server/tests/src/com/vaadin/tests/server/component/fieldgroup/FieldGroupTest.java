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

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Item;
import com.vaadin.data.Property;
import com.vaadin.data.Validator.InvalidValueException;
import com.vaadin.data.fieldgroup.FieldGroup;
import com.vaadin.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.data.util.AbstractProperty;
import com.vaadin.ui.Field;
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

    @Test
    public void commit_validationFailed_allValidationFailuresAvailable()
            throws CommitException {
        FieldGroup fieldGroup = new FieldGroup();

        fieldGroup.setItemDataSource(new TestItem());

        TextField field1 = new TextField();
        field1.setRequired(true);
        fieldGroup.bind(field1, "prop1");

        TextField field2 = new TextField();
        field2.setRequired(true);
        fieldGroup.bind(field2, "prop2");

        Set<TextField> set = new HashSet<TextField>(Arrays.asList(field1,
                field2));

        try {
            fieldGroup.commit();
            Assert.fail("No commit exception is thrown");
        } catch (CommitException exception) {
            Map<Field<?>, ? extends InvalidValueException> invalidFields = exception
                    .getInvalidFields();
            for (Entry<Field<?>, ? extends InvalidValueException> entry : invalidFields
                    .entrySet()) {
                set.remove(entry.getKey());
            }
            Assert.assertEquals(
                    "Some fields are not found in the invalid fields map", 0,
                    set.size());
            Assert.assertEquals(
                    "Invalid value exception should be thrown for each field",
                    2, invalidFields.size());
        }
    }

    private static class TestItem implements Item {

        @Override
        public Property<String> getItemProperty(Object id) {
            return new StringProperty();
        }

        @Override
        public Collection<?> getItemPropertyIds() {
            return Arrays.asList("prop1", "prop2");
        }

        @Override
        public boolean addItemProperty(Object id, Property property)
                throws UnsupportedOperationException {
            return false;
        }

        @Override
        public boolean removeItemProperty(Object id)
                throws UnsupportedOperationException {
            return false;
        }

    }

    private static class StringProperty extends AbstractProperty<String> {

        @Override
        public String getValue() {
            return null;
        }

        @Override
        public void setValue(String newValue) throws Property.ReadOnlyException {
        }

        @Override
        public Class<? extends String> getType() {
            return String.class;
        }
    }

}
