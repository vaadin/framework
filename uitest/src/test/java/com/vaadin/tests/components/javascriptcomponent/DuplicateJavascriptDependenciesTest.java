package com.vaadin.tests.components.javascriptcomponent;

import static org.junit.Assert.assertEquals;

import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class DuplicateJavascriptDependenciesTest extends SingleBrowserTest {

    @Test
    public void duplicateJavascriptsDoNotCauseProblems() {
        openTestURL();
        $(ButtonElement.class).first().click();
        assertEquals("It works", $(LabelElement.class).id("result").getText());
    }
}
