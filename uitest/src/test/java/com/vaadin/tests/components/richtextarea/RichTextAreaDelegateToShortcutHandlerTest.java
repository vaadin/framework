package com.vaadin.tests.components.richtextarea;

import java.util.List;

import com.vaadin.testbench.elements.RichTextAreaElement;
import com.vaadin.tests.tb3.MultiBrowserTest;
import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.remote.DesiredCapabilities;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

public class RichTextAreaDelegateToShortcutHandlerTest
        extends MultiBrowserTest {

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersExcludingPhantomJS();
    }

    @Test
    public void shouldDelegateToShortcutActionHandler() {
        openTestURL();

        WebElement textAreaEditor = $(RichTextAreaElement.class).first()
                .getEditorIframe();
        textAreaEditor.sendKeys("Test");
        textAreaEditor.sendKeys(Keys.ENTER);

        assertThat("Shortcut handler has not been invoked", getLogRow(0),
                containsString("ShortcutHandler invoked Test"));

        textAreaEditor.sendKeys(Keys.chord(Keys.SHIFT, Keys.ENTER));
        textAreaEditor.sendKeys("another row");
        textAreaEditor.sendKeys(Keys.ENTER);

        assertThat("Shortcut handler has not been invoked", getLogRow(0),
                containsString("ShortcutHandler invoked Test\nanother row"));
    }
}
