package com.vaadin.tests.components.textfield;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;

import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class RemoveTextChangeListenerTest extends MultiBrowserTest {

    @Test
    public void serverValueIsUpdated() {
        openTestURL();

        TextFieldElement textfield = $(TextFieldElement.class).first();

        textfield.sendKeys("f");

        assertThat(textfield.getValue(), is("f"));
    }

}