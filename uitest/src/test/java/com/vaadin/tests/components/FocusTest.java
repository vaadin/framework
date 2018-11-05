package com.vaadin.tests.components;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.TestBenchElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public abstract class FocusTest extends MultiBrowserTest {

    protected boolean isFocusInsideElement(TestBenchElement element) {
        WebElement focused = getFocusedElement();
        assertNotNull(focused);
        String id = focused.getAttribute("id");
        assertTrue("Focused element should have a non-empty id",
                id != null && !"".equals(id));
        return element.isElementPresent(By.id(id));
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        // Focus does not move when expected with Selenium/TB and Firefox 45
        return getBrowsersExcludingFirefox();
    }

}
