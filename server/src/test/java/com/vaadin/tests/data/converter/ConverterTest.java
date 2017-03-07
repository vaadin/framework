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
package com.vaadin.tests.data.converter;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Converter;
import com.vaadin.data.Result;
import com.vaadin.data.ValueContext;
import com.vaadin.server.SerializableFunction;

public class ConverterTest {

    SerializableFunction<String, Result<String>> toModel = presentation -> {
        if (presentation.startsWith("presentation-")) {
            return Result.ok(presentation.substring("presentation-".length()));
        } else {
            return Result.error("invalid prefix: " + presentation);
        }
    };

    SerializableFunction<String, String> toPresentation = model -> "presentation-"
            + model;

    Converter<String, String> converter = Converter.from(toModel,
            toPresentation);

    @Test
    public void basicConversion() {
        Assert.assertEquals("presentation-123",
                converter.convertToPresentation("123", new ValueContext()));
        Assert.assertEquals("123",
                converter.convertToModel("presentation-123", new ValueContext())
                        .getOrThrow(msg -> new AssertionError(msg)));
    }
}
