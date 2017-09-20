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
package com.vaadin.data;

import java.time.LocalDate;

import org.junit.Assert;
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

        Assert.assertFalse(nullable.getOptionalValue().isPresent());

        nullable.setValue(LocalDate.now());

        assert !nullable.isEmpty();

        Assert.assertSame(nullable.getValue(),
                nullable.getOptionalValue().get());
    }

    @Test
    public void getOptionalValue_nonNullableHasValue() {
        HasValue<String> nonNullable = new TextField();

        // Not using Assert since we're only verifying that TextField is working
        // in a way appropriate for this test
        assert nonNullable.isEmpty();
        assert nonNullable.getValue() != null;

        Assert.assertFalse(nonNullable.getOptionalValue().isPresent());

        nonNullable.setValue("foo");

        assert !nonNullable.isEmpty();

        Assert.assertSame(nonNullable.getValue(),
                nonNullable.getOptionalValue().get());
    }
}
