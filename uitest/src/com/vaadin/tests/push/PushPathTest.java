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
package com.vaadin.tests.push;

import org.junit.Assert;
import org.junit.Test;

import com.vaadin.tests.tb3.WebsocketTest;

public class PushPathTest extends WebsocketTest {

    private static final int TEN_SECONDS_IN_MS = 10 * 1000;

    @Test
    public void testCustomPushPath() throws InterruptedException {
        openTestURL();
        sleep(TEN_SECONDS_IN_MS);
        Assert.assertEquals(vaadinElementById(PushPath.PUSH_PATH_LABEL_ID)
                .getText(), PushPath.PUSH_PATH_LABEL_TEXT);
    }

    @Override
    public String getDeploymentPath() {
        Class<?> uiClass = getUIClass();
        return "/run-pushpath/" + uiClass.getCanonicalName();
    }

}
