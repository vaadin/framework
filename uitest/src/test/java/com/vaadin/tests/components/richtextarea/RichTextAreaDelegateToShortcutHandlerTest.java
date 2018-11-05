package com.vaadin.tests.components.richtextarea;

import static org.hamcrest.Matchers.containsString;
import static org.junit.Assert.assertThat;

import org.junit.Test;
import org.openqa.selenium.Keys;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.RichTextAreaElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class RichTextAreaDelegateToShortcutHandlerTest
        extends MultiBrowserTest {

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
