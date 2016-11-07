package com.vaadin.test.defaultwidgetset;

import java.util.List;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Rule;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.phantomjs.PhantomJSDriver;

import com.vaadin.testbench.ScreenshotOnFailureRule;
import com.vaadin.testbench.TestBenchTestCase;
import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TextFieldElement;

public abstract class AbstractWidgetSetIT extends TestBenchTestCase {

    @Rule
    public ScreenshotOnFailureRule rule = new ScreenshotOnFailureRule(this,
            true);

    @Before
    public void setup() {
        // Screenshot rule tears down the driver
        setDriver(new PhantomJSDriver());
    }

    protected void testAppStartsUserCanInteract(String expectedWidgetSet) {
        testAppStartsUserCanInteract(expectedWidgetSet, false);
    }

    protected void testAppStartsUserCanInteract(String expectedWidgetSet,
            boolean debug) {
        String url = "http://localhost:8080";
        if (debug) {
            url += "?debug";
        }
        getDriver().get(url);

        TextFieldElement nameInput = $(TextFieldElement.class).first();
        nameInput.setValue("John Dåe");

        $(ButtonElement.class).first().click();

        Assert.assertEquals("Label shown", 2,
                $(LabelElement.class).all().size());

        Assert.assertEquals("Thanks John Dåe, it works!",
                $(LabelElement.class).get(1).getText());

        Assert.assertEquals(expectedWidgetSet,
                findElement(By.id("widgetsetinfo")).getText());

    }

    protected void assertNoUnknownComponentShown() {
        Assert.assertEquals(0,
                findElements(By.className("vaadin-unknown-caption")).size());
    }

    protected void assertUnknownComponentShown(String componentClass) {
        WebElement unknownComponentCaption = findElement(
                By.className("vaadin-unknown-caption"));
        Assert.assertTrue(unknownComponentCaption.getText().contains(
                "does not contain implementation for " + componentClass));
    }

    protected void assertHasDebugMessage(String message) {
        List<WebElement> elements = getDriver().findElements(
                By.xpath("//span[@class='v-debugwindow-message']"));
        boolean found = false;
        for (WebElement element : elements) {
            if (element.getText().contains(message)) {
                found = true;
                break;
            }
        }
        Assert.assertTrue(
                "Cannot find debug message containing '" + message + "'",
                found);
    }

}
