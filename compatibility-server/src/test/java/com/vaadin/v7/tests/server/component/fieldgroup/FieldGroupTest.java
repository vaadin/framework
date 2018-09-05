package com.vaadin.v7.tests.server.component.fieldgroup;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.junit.Test;

import com.vaadin.v7.data.Item;
import com.vaadin.v7.data.Property;
import com.vaadin.v7.data.Validator.InvalidValueException;
import com.vaadin.v7.data.fieldgroup.FieldGroup;
import com.vaadin.v7.data.fieldgroup.FieldGroup.CommitException;
import com.vaadin.v7.data.util.AbstractProperty;
import com.vaadin.v7.ui.Field;
import com.vaadin.v7.ui.TextField;

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

        assertTrue("Field is not read only", field.isReadOnly());
    }

    @Test
    public void setReadOnly_writableAndNoDataSource_fieldIsWritable() {
        FieldGroup fieldGroup = new FieldGroup();

        TextField field = new TextField();
        fieldGroup.bind(field, "property");

        fieldGroup.setReadOnly(false);

        assertFalse("Field is not writable", field.isReadOnly());
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

        Set<TextField> set = new HashSet<TextField>(
                Arrays.asList(field1, field2));

        try {
            fieldGroup.commit();
            fail("No commit exception is thrown");
        } catch (CommitException exception) {
            Map<Field<?>, ? extends InvalidValueException> invalidFields = exception
                    .getInvalidFields();
            for (Entry<Field<?>, ? extends InvalidValueException> entry : invalidFields
                    .entrySet()) {
                set.remove(entry.getKey());
            }
            assertEquals("Some fields are not found in the invalid fields map",
                    0, set.size());
            assertEquals(
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
        public void setValue(String newValue)
                throws Property.ReadOnlyException {
        }

        @Override
        public Class<? extends String> getType() {
            return String.class;
        }
    }

}
