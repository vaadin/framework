package com.vaadin.tests.components.checkboxgroup;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.CheckBoxGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class CheckBoxGroupItemDisabledTest extends MultiBrowserTest {

    @Test
    public void itemDisabledOnInit() {
        openTestURL();
        List<WebElement> options = $(CheckBoxGroupElement.class).first()
                .getOptionElements();
        options.stream().forEach(option -> {
            Integer value = Integer.parseInt(option.getText());
            boolean disabled = !CheckBoxGroupItemDisabled.ENABLED_PROVIDER
                    .test(value);
            assertEquals(
                    "Unexpected status of v-disabled stylename for item "
                            + value,
                    disabled,
                    option.getAttribute("class").contains("v-disabled"));
        });
    }
}
