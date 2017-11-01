package com.vaadin.tests.components.textarea;

import static com.vaadin.tests.components.textarea.TextAreaSizeResetted.TEXTAREAHEIGHT;
import static com.vaadin.tests.components.textarea.TextAreaSizeResetted.TEXTAREAWIDTH;
import static org.junit.Assert.assertEquals;

import java.util.List;

import org.junit.Test;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.remote.DesiredCapabilities;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.testbench.elements.TextAreaElement;
import com.vaadin.testbench.elements.TextFieldElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class TextAreaSizeResettedTest extends MultiBrowserTest {

    private final int OFFSET = 100;

    @Override
    public void setup() throws Exception {
        super.setup();

        openTestURL();
    }

    @Override
    public List<DesiredCapabilities> getBrowsersToTest() {
        return getBrowsersExcludingIE(); // IE8-11 don't support CSS resize.
    }

    @Test
    public void textAreaIsNotResizedOnBlur() {

        resizeAndAssertTextAreaTo(TEXTAREAHEIGHT, OFFSET);

        getTextArea().sendKeys("foo");

        moveFocusOutsideTextArea();

        // We can't use a waitUntil to check the text area size here, because it
        // won't release the focus from
        // the text area, so we need to do use something else. This workaround
        // uses a label which is updated to indicate
        // polling, which should trigger a resize.
        waitUntilPollingOccurs();

        assertEquals(TEXTAREAHEIGHT + OFFSET, getTextAreaHeight());
        assertEquals(TEXTAREAWIDTH + OFFSET, getTextAreaWidth());

        waitUntilPollingOccurs();
    }

    private void moveFocusOutsideTextArea() {
        $(TextFieldElement.class).first().focus();
    }

    private void resizeAndAssertTextAreaTo(int size, int offset) {
        // Sanity check
        assertEquals(size, getTextAreaHeight());
        resizeTextAreaBy(offset);

        assertEquals(size + offset, getTextAreaHeight());
    }

    private void resizeTextAreaBy(int offset) {
        int resizeHandlerOffset = 10;
        new Actions(getDriver())
                .moveToElement(getTextArea(),
                        TEXTAREAWIDTH - resizeHandlerOffset,
                        TEXTAREAHEIGHT - resizeHandlerOffset)
                .clickAndHold().moveByOffset(offset, offset).release().build()
                .perform();
    }

    @Test
    public void textAreaWidthIsPresevedOnHeightResize() {
        resizeAndAssertTextAreaTo(TEXTAREAHEIGHT, OFFSET);

        changeHeightTo(TEXTAREAHEIGHT + OFFSET + OFFSET);

        assertEquals(TEXTAREAWIDTH + OFFSET, getTextAreaWidth());
        assertEquals(TEXTAREAHEIGHT + OFFSET + OFFSET, getTextAreaHeight());
    }

    private void changeHeightTo(int offset) {
        $(TextFieldElement.class).first().sendKeys(String.valueOf(offset));
        $(ButtonElement.class).first().click();
    }

    private void waitUntilPollingOccurs() {
        final String timestamp = getPollTimestamp();

        waitUntil(input -> !timestamp.equals(getPollTimestamp()));
    }

    private String getPollTimestamp() {
        return $(LabelElement.class).id("pollIndicator").getText();
    }

    private int getTextAreaHeight() {
        return getTextAreaSize().getHeight();
    }

    private int getTextAreaWidth() {
        return getTextAreaSize().getWidth();
    }

    private Dimension getTextAreaSize() {
        return getTextArea().getSize();
    }

    private TextAreaElement getTextArea() {
        return $(TextAreaElement.class).first();
    }
}
