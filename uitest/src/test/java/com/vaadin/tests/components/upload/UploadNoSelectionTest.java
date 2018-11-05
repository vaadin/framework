package com.vaadin.tests.components.upload;

import static org.junit.Assert.assertEquals;

import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class UploadNoSelectionTest extends MultiBrowserTest {

    @Test
    public void testUploadNoSelection() throws Exception {
        openTestURL();

        // empty content is populated by com.vaadin.tests.util.Log
        assertEquals(" ", getLogRow(0));

        getSubmitButton().click();

        // expecting empty file name
        assertLogRow(0, 4, UploadNoSelection.FILE_NAME_PREFIX);
        // expecting 0-length file
        assertLogRow(1, 3, UploadNoSelection.FILE_LENGTH_PREFIX + " " + 0);
        assertLogRow(2, 2, UploadNoSelection.UPLOAD_FINISHED);
        assertLogRow(3, 1, UploadNoSelection.RECEIVING_UPLOAD);
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
