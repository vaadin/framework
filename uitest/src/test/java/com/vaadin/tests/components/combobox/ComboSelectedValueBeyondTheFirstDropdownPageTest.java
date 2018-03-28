package com.vaadin.tests.components.combobox;

import com.vaadin.testbench.elements.ComboBoxElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.is;

@SuppressWarnings("serial")
public class ComboSelectedValueBeyondTheFirstDropdownPageTest
        extends MultiBrowserTest {

    @Test
    public void valueOnSecondPageIsSelected() {
        openTestURL();

        ComboBoxElement comboBoxWebElement = $(ComboBoxElement.class).first();

        comboBoxWebElement.openNextPage();
        comboBoxWebElement.selectByText("Item 19");

        assertThat($(LabelElement.class).id("value").getText(), is("Item 19"));
    }
}
