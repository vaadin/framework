package com.vaadin.tests.components.composite;

import org.junit.Test;

import com.vaadin.testbench.By;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class CompositePanelCaptionUITest extends SingleBrowserTest {

    @Test
    public void compositeDoesNotDuplicateCaption() {
        openTestURL();
        assertElementNotPresent(By.className("v-caption"));
    }
}
