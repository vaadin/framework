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
package com.vaadin.tests.widgetset.server.gwtrpc;

import org.junit.Test;
import org.openqa.selenium.By;

import com.vaadin.tests.tb3.MultiBrowserTest;
import com.vaadin.tests.widgetset.client.gwtrpc.GwtRpcButtonConnector;

/**
 * Test the GWT RPC with Vaadin DevMode. See #11709.
 *
 * @author Vaadin Ltd
 */
public class GwtRpcTest extends MultiBrowserTest {

    @Test
    public void testGwtRpc() {
        openTestURL();

        getDriver().findElement(By.id(GwtRpc.BUTTON_ID)).click();

        By label = By.id(GwtRpcButtonConnector.SUCCESS_LABEL_ID);

        waitForElementVisible(label);
        getDriver().findElement(label);
    }

}
