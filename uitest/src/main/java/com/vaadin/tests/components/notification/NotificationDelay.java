/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.tests.components.notification;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;

/**
 * UI for notification delay test.
 *
 * @author Vaadin Ltd
 */
public class NotificationDelay extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        Notification notification = new Notification("Foo",
                Type.HUMANIZED_MESSAGE);
        notification.setDelayMsec(500);
        notification.show(getPage());
    }

    @Override
    protected String getTestDescription() {
        return "Notification should be closed after delay";
    }

    @Override
    protected Integer getTicketNumber() {
        return 14689;
    }

}
