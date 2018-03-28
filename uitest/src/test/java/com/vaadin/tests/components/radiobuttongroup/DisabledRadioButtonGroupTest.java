package com.vaadin.tests.components.radiobuttongroup;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * @author Vaadin Ltd
 *
 */
public class DisabledRadioButtonGroupTest extends MultiBrowserTest {

    @Test
    public void initialDataInDisabledCheckBoxGroup() {
        openTestURL();
        List<String> options = $(RadioButtonGroupElement.class).first()
                .getOptions();
        assertEquals(3, options.size());
        assertEquals("a", options.get(0));
        assertEquals("b", options.get(1));
        assertEquals("c", options.get(2));
    }

}
