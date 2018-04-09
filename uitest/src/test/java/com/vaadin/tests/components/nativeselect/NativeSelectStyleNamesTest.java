package com.vaadin.tests.components.nativeselect;

import static org.junit.Assert.assertEquals;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import org.junit.Test;

import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class NativeSelectStyleNamesTest extends SingleBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return NativeSelectInit.class;
    }

    @Test
    public void correctStyleNames() {
        openTestURL();

        Set<String> expected = Stream.of("v-select", "v-widget")
                .collect(Collectors.toSet());
        assertEquals(expected,
                $(NativeSelectElement.class).first().getClassNames());
    }
}
