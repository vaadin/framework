package com.vaadin.tests.layouts.customlayout;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.MatcherAssert.assertThat;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class DefaultLocationInCustomLayoutTest extends MultiBrowserTest {

    @Test
    public void buttonExistsInLayout() {
        openTestURL();

        // We don't use TestBench's ElementQuery here because we need to check
        // the DOM for buttons existence.
        assertThat(
                driver.findElements(
                        By.id(DefaultLocationInCustomLayout.BUTTON_ID)).size(),
                is(1));
    }
}
