package com.vaadin.tests.server.component.abstractfield;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import org.easymock.EasyMock;
import org.junit.Test;

import com.vaadin.legacy.data.Validator;
import com.vaadin.legacy.ui.LegacyAbstractField;

public class AbsFieldValidatorsTest {

    LegacyAbstractField<Object> field = new LegacyAbstractField<Object>() {
        @Override
        public Class getType() {
            return Object.class;
        }
    };

    Validator validator = EasyMock.createMock(Validator.class);
    Validator validator2 = EasyMock.createMock(Validator.class);

    @Test
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

    @Test
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

    @Test
    public void testRemoveAllValidators() {
        field.addValidator(validator);
        field.addValidator(validator2);

        field.removeAllValidators();
        assertNotNull(field.getValidators());
        assertEquals(0, field.getValidators().size());
        assertFalse(field.getValidators().contains(validator));
        assertFalse(field.getValidators().contains(validator2));
    }

    @Test
    public void nonImmediateFieldWithValidator() {
        field.setImmediate(false);
        field.addValidator(validator);
        assertFalse("field should be non-immediate because explicitly set",
                field.isImmediate());
    }

}
