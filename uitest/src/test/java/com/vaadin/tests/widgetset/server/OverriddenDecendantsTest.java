package com.vaadin.tests.widgetset.server;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;

import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

/**
 * Class for unit testing that @DelegateToWidget works on derived widget states.
 *
 * @since
 * @author Vaadin Ltd
 */
public class OverriddenDecendantsTest extends MultiBrowserTest {

    @Test
    public void allExtendingFieldsShouldGetRowsFromTextAreaStateAnnotation()
            throws InterruptedException {
        openTestURL();

        List<TextAreaElement> textAreas = $(TextAreaElement.class).all();

        assertEquals("Did not contain all 3 text areas", 3, textAreas.size());

        for (TextAreaElement area : textAreas) {
            assertEquals("Text area was missing rows", "10",
                    area.getAttribute("rows"));
        }

    }
}
