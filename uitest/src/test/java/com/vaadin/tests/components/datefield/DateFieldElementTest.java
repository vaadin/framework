package com.vaadin.tests.components.datefield;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.Is.is;

import org.junit.Test;

import com.vaadin.testbench.elements.AbstractDateFieldElement;
import com.vaadin.testbench.elements.DateFieldElement;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class DateFieldElementTest extends SingleBrowserTest {

    @Test
    public void dateFieldElementIsLocated() {
        openTestURL();

        assertThat($(DateFieldElement.class).all().size(), is(2));
        assertThat($(AbstractDateFieldElement.class).all().size(), is(2));
    }

    @Override
    protected Class<?> getUIClass() {
        return DateFieldElementUI.class;
    }
}
