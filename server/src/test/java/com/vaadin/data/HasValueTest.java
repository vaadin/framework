package com.vaadin.data;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertSame;

import java.time.LocalDate;

import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.ui.DateField;
import com.vaadin.ui.TextField;

/**
 * @author Vaadin Ltd
 *
 */
public class HasValueTest {

    public abstract static class TestHasValue implements HasValue<String> {
        @Override
        public void clear() {
            HasValue.super.clear();
        }
    }

    @Test
    public void clear() {
        TestHasValue hasValue = Mockito.mock(TestHasValue.class);
        Mockito.doCallRealMethod().when(hasValue).clear();
        String value = "foo";
        Mockito.when(hasValue.getEmptyValue()).thenReturn(value);

        hasValue.clear();

        Mockito.verify(hasValue).setValue(value);
    }

    @Test
    public void getOptionalValue_nullableHasValue() {
        HasValue<LocalDate> nullable = new DateField();

        // Not using Assert since we're only verifying that DateField is working
        // in a way appropriate for this test
        assert nullable.isEmpty();
        assert nullable.getValue() == null;

        assertFalse(nullable.getOptionalValue().isPresent());

        nullable.setValue(LocalDate.now());

        assert !nullable.isEmpty();

        assertSame(nullable.getValue(), nullable.getOptionalValue().get());
    }

    @Test
    public void getOptionalValue_nonNullableHasValue() {
        HasValue<String> nonNullable = new TextField();

        // Not using Assert since we're only verifying that TextField is working
        // in a way appropriate for this test
        assert nonNullable.isEmpty();
        assert nonNullable.getValue() != null;

        assertFalse(nonNullable.getOptionalValue().isPresent());

        nonNullable.setValue("foo");

        assert !nonNullable.isEmpty();

        assertSame(nonNullable.getValue(),
                nonNullable.getOptionalValue().get());
    }
}
