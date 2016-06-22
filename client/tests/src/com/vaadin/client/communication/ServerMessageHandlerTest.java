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
package com.vaadin.client.communication;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Vaadin Ltd
 */
public class ServerMessageHandlerTest {

    @Test
    public void unwrapValidJson() {
        String payload = "{'foo': 'bar'}";
        Assert.assertEquals(payload,
                MessageHandler.stripJSONWrapping("for(;;);[" + payload + "]"));

    }

    @Test
    public void unwrapUnwrappedJson() {
        String payload = "{'foo': 'bar'}";
        Assert.assertNull(MessageHandler.stripJSONWrapping(payload));

    }

    @Test
    public void unwrapNull() {
        Assert.assertNull(MessageHandler.stripJSONWrapping(null));

    }

    @Test
    public void unwrapEmpty() {
        Assert.assertNull(MessageHandler.stripJSONWrapping(""));

    }
}
