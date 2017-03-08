/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.tests.applicationservlet;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NativeSelectElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class SystemMessagesTest extends MultiBrowserTest {

    @Test
    public void testFinnishLocaleInSystemErrorMessage() throws Exception {
        openTestURL();
        verifyError("fi_FI");
    }

    @Test
    public void testGermanLocaleInSystemErrorMessage() throws Exception {
        openTestURL();
        $(NativeSelectElement.class).first().selectByText("de_DE");
        verifyError("de_DE");
    }

    private void verifyError(String locale) {
        $(ButtonElement.class).first().click();
        NotificationElement notification = $(NotificationElement.class).first();
        Assert.assertEquals("Incorrect notification caption,",
                notification.getCaption(), "Internal error");
        Assert.assertEquals("Incorrect notification description,",
                notification.getDescription(),
                "MessagesInfo locale: " + locale);
    }
}
