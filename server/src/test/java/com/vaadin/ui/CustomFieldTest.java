package com.vaadin.ui;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.Iterator;
import java.util.NoSuchElementException;

import org.junit.Test;

public class CustomFieldTest {

    public static class TestCustomField extends CustomField<String> {

        private String value = "initial";
        private Button button;

        @Override
        public String getValue() {
            return value;
        }

        @Override
        protected Component initContent() {
            button = new Button("Content");
            return button;
        }

        @Override
        protected void doSetValue(String value) {
            this.value = value;

        }

    }

    @Test(expected = NoSuchElementException.class)
    public void iterator() {
        TestCustomField field = new TestCustomField();
        // Needs to trigger initContent somehow as
        // iterator() can't do it even though it should...
        field.getContent();
        Iterator<Component> iterator = field.iterator();
        assertNotNull(iterator);
        assertTrue(iterator.hasNext());
        assertEquals(field.button, iterator.next());
        assertFalse(iterator.hasNext());
        iterator.next();
    }
}
