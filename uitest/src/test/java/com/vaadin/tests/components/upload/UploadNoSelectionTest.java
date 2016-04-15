/*
 * Copyright 2000-2014 Vaadin Ltd.
 * 
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.upload;

import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;

import com.vaadin.tests.tb3.MultiBrowserTest;

public class UploadNoSelectionTest extends MultiBrowserTest {

    @Test
    public void testUploadNoSelection() throws Exception {
        openTestURL();

        // empty content is populated by com.vaadin.tests.util.Log
        Assert.assertEquals(" ", getLogRow(0));

        getSubmitButton().click();

        // expecting empty file name
        assertLogRow(0, 4, UploadNoSelection.FILE_NAME_PREFIX);
        // expecting 0-length file
        assertLogRow(1, 3, UploadNoSelection.FILE_LENGTH_PREFIX + " " + 0);
        assertLogRow(2, 2, UploadNoSelection.UPLOAD_FINISHED);
        assertLogRow(3, 1, UploadNoSelection.RECEIVING_UPLOAD);
    }

    private WebElement getSubmitButton() {
        WebElement element = getDriver().findElement(
                By.id(UploadNoSelection.UPLOAD_ID));
        WebElement submitButton = element.findElement(By.className("v-button"));
        return submitButton;
    }

    private void assertLogRow(int index, int expentedRowNo,
            String expectedValueWithoutRowNo) {
        Assert.assertEquals(expentedRowNo + ". " + expectedValueWithoutRowNo,
                getLogRow(index));
    }
}
