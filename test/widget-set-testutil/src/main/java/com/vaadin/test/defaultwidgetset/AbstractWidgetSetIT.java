package com.vaadin.test.defaultwidgetset;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import io.github.bonigarcia.wdm.WebDriverManager;
import java.util.List;

import org.junit.Before;
import org.junit.BeforeClass;
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

    @BeforeClass
    public static void setupClass() {
        WebDriverManager.phantomjs().setup();
    }

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

        assertEquals("Label shown", 2, $(LabelElement.class).all().size());

        assertEquals("Thanks John Dåe, it works!",
                $(LabelElement.class).get(1).getText());

        assertEquals(expectedWidgetSet,
                findElement(By.id("widgetsetinfo")).getText());

    }

    protected void assertNoUnknownComponentShown() {
        assertEquals(0,
                findElements(By.className("vaadin-unknown-caption")).size());
    }

    protected void assertUnknownComponentShown(String componentClass) {
        WebElement unknownComponentCaption = findElement(
                By.className("vaadin-unknown-caption"));
        assertTrue(unknownComponentCaption.getText().contains(
                "does not contain implementation for " + componentClass));
    }

    protected void assertHasDebugMessage(String message) {
        // Make sure the correct debug window tab is open.
        findElements(By.className("v-debugwindow-tab")).get(0).click();

        List<WebElement> elements = getDriver().findElements(
                By.xpath("//span[@class='v-debugwindow-message']"));
        boolean found = false;
        for (WebElement element : elements) {
            if (element.getText().contains(message)) {
                found = true;
                break;
            }
        }
        assertTrue("Cannot find debug message containing '" + message + "'",
                found);
    }

}
