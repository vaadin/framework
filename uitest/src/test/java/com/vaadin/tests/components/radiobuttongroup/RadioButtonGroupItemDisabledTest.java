package com.vaadin.tests.components.radiobuttongroup;

import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.RadioButtonGroupElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class RadioButtonGroupItemDisabledTest extends MultiBrowserTest {

    @Test
    public void itemDisabledOnInit() {
        openTestURL();
        List<WebElement> options = $(RadioButtonGroupElement.class).first()
                .getOptionElements();
        options.stream().forEach(option -> {
            Integer value = Integer.parseInt(option.getText());
            boolean disabled = !RadioButtonGroupItemDisabled.ENABLED_PROVIDER
                    .test(value);
            assertEquals(
                    "Unexpected status of v-disabled stylename for item "
                            + value,
                    disabled,
                    option.getAttribute("class").contains("v-disabled"));
        });
    }
}
