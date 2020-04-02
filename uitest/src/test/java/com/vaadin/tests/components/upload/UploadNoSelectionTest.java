package com.vaadin.tests.components.upload;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class UploadNoSelectionTest extends MultiBrowserTest {

    @Test
    public void testUploadNoSelection() throws Exception {
        openTestURL();

        // empty content is populated by com.vaadin.tests.util.Log
        assertEquals(" ", getLogRow(0));

        WebElement submitButton = getSubmitButton();
        assertTrue("Upload button should be disabled when no selection.",
                hasCssClass(submitButton, "v-disabled"));

        submitButton.click();

        // clicking the disabled default button doesn't do a thing
        assertEquals(" ", getLogRow(0));

        $(ButtonElement.class).id("programmatic").click();

        // neither does triggering upload programmatically
        assertEquals(" ", getLogRow(0));

        // add an extension that allows upload without filename
        $(ButtonElement.class).id("extend").click();

        submitButton.click();

        // expecting empty file name
        assertLogRow(0, 4, UploadNoSelection.FILE_NAME_PREFIX);
        // expecting 0-length file
        assertLogRow(1, 3, UploadNoSelection.FILE_LENGTH_PREFIX + " " + 0);
        assertLogRow(2, 2, UploadNoSelection.UPLOAD_FINISHED);
        assertLogRow(3, 1, UploadNoSelection.RECEIVING_UPLOAD);

        // and the same programmatically
        $(ButtonElement.class).id("programmatic").click();

        // expecting empty file name
        assertLogRow(0, 8, UploadNoSelection.FILE_NAME_PREFIX);
        // expecting 0-length file
        assertLogRow(1, 7, UploadNoSelection.FILE_LENGTH_PREFIX + " " + 0);
        assertLogRow(2, 6, UploadNoSelection.UPLOAD_FINISHED);
        assertLogRow(3, 5, UploadNoSelection.RECEIVING_UPLOAD);

    }

    private WebElement getSubmitButton() {
        WebElement element = getDriver()
                .findElement(By.id(UploadNoSelection.UPLOAD_ID));
        WebElement submitButton = element.findElement(By.className("v-button"));
        return submitButton;
    }

    private void assertLogRow(int index, int expentedRowNo,
            String expectedValueWithoutRowNo) {
        assertEquals(expentedRowNo + ". " + expectedValueWithoutRowNo,
                getLogRow(index));
    }
}
