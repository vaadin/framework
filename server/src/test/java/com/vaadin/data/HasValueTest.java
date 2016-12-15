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

import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.data.HasValue;

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
}
