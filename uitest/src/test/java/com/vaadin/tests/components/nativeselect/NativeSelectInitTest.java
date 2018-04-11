package com.vaadin.tests.components.nativeselect;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class NativeSelectInitTest extends MultiBrowserTest {

    @Test
    public void secondItemIsSelected() {
        openTestURL();

        String selected = $(NativeSelectElement.class).first().getValue();
        assertEquals("Bar", selected);
    }
}
