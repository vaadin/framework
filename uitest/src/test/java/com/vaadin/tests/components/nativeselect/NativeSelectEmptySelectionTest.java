package com.vaadin.tests.components.nativeselect;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import org.junit.Test;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class NativeSelectEmptySelectionTest extends MultiBrowserTest {

    @Test
    public void checkEmptySelection() {
        openTestURL();

        checkOptions("empty");

        // change the caption
        $(ButtonElement.class).first().click();
        checkOptions("updated");

        // disable empty caption
        $(ButtonElement.class).get(1).click();
        checkOptions(null);

        // enable back
        $(ButtonElement.class).get(2).click();
        checkOptions("updated");
    }

    private void checkOptions(String emptyCaption) {
        NativeSelectElement select = $(NativeSelectElement.class).first();
        Set<String> originalOptions = IntStream.range(1, 50)
                .mapToObj(index -> String.valueOf(index))
                .collect(Collectors.toSet());
        Set<String> options = select.getOptions().stream()
                .map(TestBenchElement::getText).collect(Collectors.toSet());
        if (emptyCaption == null) {
            assertEquals(49, options.size());
            assertTrue(options.containsAll(originalOptions));
        } else {
            options.contains(emptyCaption);
            assertEquals(50, options.size());
            assertTrue(options.containsAll(originalOptions));
        }
    }
}
