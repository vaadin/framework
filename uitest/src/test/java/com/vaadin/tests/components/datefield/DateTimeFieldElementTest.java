package com.vaadin.tests.components.datefield;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;

import com.vaadin.testbench.customelements.DateTimeFieldElement;
import com.vaadin.testbench.customelements.InlineDateTimeFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class DateTimeFieldElementTest extends SingleBrowserTest {

    @Test
    public void dateFieldElementIsLocated() {
        openTestURL();

        assertThat($(DateTimeFieldElement.class).all().size(), is(1));
        assertThat($(InlineDateTimeFieldElement.class).all().size(), is(1));
    }

    @Override
    protected Class<?> getUIClass() {
        return DateTimeFieldElementUI.class;
    }
}