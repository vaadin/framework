package com.vaadin.tests.server.component.abstractfield;

import junit.framework.TestCase;

import org.easymock.EasyMock;

import com.vaadin.data.Validator;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Field;

public class AbsFieldValidators extends TestCase {

    Field<Object> field = new AbstractField<Object>() {
        @Override
        public Class getType() {
            return Object.class;
        }
    };

    Validator validator = EasyMock.createMock(Validator.class);
    Validator validator2 = EasyMock.createMock(Validator.class);

    public void testAddValidator() {
        assertNotNull(field.getValidators());
        assertEquals(0, field.getValidators().size());

        field.addValidator(validator);
        assertEquals(1, field.getValidators().size());
        assertTrue(field.getValidators().contains(validator));

        field.addValidator(validator2);
        assertEquals(2, field.getValidators().size());
        assertTrue(field.getValidators().contains(validator));
        assertTrue(field.getValidators().contains(validator2));
    }

    public void testRemoveValidator() {
        field.addValidator(validator);
        field.addValidator(validator2);

        field.removeValidator(validator);
        assertNotNull(field.getValidators());
        assertEquals(1, field.getValidators().size());
        assertFalse(field.getValidators().contains(validator));
        assertTrue(field.getValidators().contains(validator2));

        field.removeValidator(validator2);
        assertNotNull(field.getValidators());
        assertEquals(0, field.getValidators().size());
        assertFalse(field.getValidators().contains(validator));
        assertFalse(field.getValidators().contains(validator2));
    }

    public void testRemoveAllValidators() {
        field.addValidator(validator);
        field.addValidator(validator2);

        field.removeAllValidators();
        assertNotNull(field.getValidators());
        assertEquals(0, field.getValidators().size());
        assertFalse(field.getValidators().contains(validator));
        assertFalse(field.getValidators().contains(validator2));
    }
}
