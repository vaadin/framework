package com.vaadin.tests.applicationservlet;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.CheckBoxElement;
import com.vaadin.tests.components.label.LabelModes;
import com.vaadin.tests.tb3.SingleBrowserTest;

public class ServletWithResourcesTest extends SingleBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return LabelModes.class;
    }

    @Override
    protected String getDeploymentPath(Class<?> uiClass) {
        return super.getDeploymentPath(uiClass).replaceAll("/run/",
                "/servlet-with-resources/");
    }

    @Test
    public void servletServesResources() {
        openTestURL();
        assertEquals("Enabled", $(CheckBoxElement.class).first().getCaption());

        List<WebElement> links = findElements(By.xpath("//head/link"));
        for (WebElement link : links) {
            String href = link.getAttribute("href");
            assertTrue("href '" + href
                    + "' should contain '/servlet-with-resources/VAADIN'",
                    href.contains("/servlet-with-resources/VAADIN"));
        }

        List<WebElement> scripts = findElements(By.xpath("//head/script"));
        for (WebElement script : scripts) {
            String src = script.getAttribute("src");
            assertTrue("src '" + src
                    + "' should contain '/servlet-with-resources/VAADIN'",
                    src.contains("/servlet-with-resources/VAADIN"));
        }

    }

}
