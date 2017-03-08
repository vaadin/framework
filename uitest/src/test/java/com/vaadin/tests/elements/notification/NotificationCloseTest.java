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
package com.vaadin.tests.elements.notification;

import java.util.List;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.testbench.elements.ButtonElement;
import com.vaadin.testbench.elements.NotificationElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class NotificationCloseTest extends MultiBrowserTest {

    @Override
    protected Class<?> getUIClass() {
        return NotificationGetTypeAndDescription.class;
    }

    @Test
    public void testWarning() {
        testClose(0);
    }

    @Test
    public void testError() {
        testClose(1);
    }

    @Test
    public void testHumanized() {
        testClose(2);
    }

    @Test
    public void testTrayNotification() {
        testClose(3);
    }

    private void testClose(int index) {
        openTestURL();
        String id = "button" + index;
        ButtonElement btn = $(ButtonElement.class).id(id);
        // show notification
        btn.click();
        $(NotificationElement.class).get(0).close();
        List<NotificationElement> notifications = $(NotificationElement.class)
                .all();
        // check that all notifications are closed
        Assert.assertTrue("There are open notifications",
                notifications.isEmpty());
    }
}
