package com.vaadin.tests.components.textfield;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

public class TimerTriggeredTextChangeEventTest extends MultiBrowserTest {

    @Test
    public void serverValueIsUpdated() {
        openTestURL();

        TextFieldElement textfield = $(TextFieldElement.class).first();
        LabelElement serverValue = $(LabelElement.class).caption("Server:")
                                                        .first();

        textfield.sendKeys("f");

        assertThat(textfield.getValue(), is("f"));
        assertThat(serverValue.getText(), is("f"));
    }

}