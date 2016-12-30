package com.vaadin.tests.elements.progressbar;

import static org.junit.Assert.assertEquals;

import org.junit.Before;
import org.junit.Test;

import com.vaadin.testbench.elements.ProgressBarElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class ProgressBarValueTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return ProgressBarUI.class;
    }

    @Before
    public void init() {
        openTestURL();
    }

    @Test
    public void progressBar_differentValues_valuesFetchedCorrectly() {
        assertEquals(1, $(ProgressBarElement.class).id("complete").getValue(),
                0);
        assertEquals(0.5,
                $(ProgressBarElement.class).id("halfComplete").getValue(), 0);
        assertEquals(0, $(ProgressBarElement.class).id("notStarted").getValue(),
                0);
    }
}
