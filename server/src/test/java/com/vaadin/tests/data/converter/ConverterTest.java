package com.vaadin.tests.data.converter;

import java.util.function.Function;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.data.Result;
import com.vaadin.data.util.converter.Converter;

public class ConverterTest {

    Function<String, Result<String>> toModel = presentation -> {
        if (presentation.startsWith("presentation-")) {
            return Result.ok(presentation.substring("presentation-".length()));
        } else {
            return Result.error("invalid prefix: " + presentation);
        }
    };

    Function<String, String> toPresentation = model -> "presentation-" + model;

    Converter<String, String> converter = Converter.from(toModel,
            toPresentation);

    @Test
    public void basicConversion() {
        Assert.assertEquals("presentation-123",
                converter.convertToPresentation("123", null));
        Assert.assertEquals("123",
                converter.convertToModel("presentation-123", null)
                        .getOrThrow(msg -> new AssertionError(msg)));
    }

}
