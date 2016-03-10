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
package com.vaadin.server;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

import org.junit.Assert;
import org.junit.Test;

/**
 * We test that AbstractClientConnector has a suitable isThis method which is
 * needed to correctly perform an equals check between a proxy and it's
 * underlying instance.
 * 
 * @author Vaadin Ltd
 */
public class AbstractClientConnectorProxyHandlingTest {

    @Test
    public void abstractClientConnectorTest() {
        try {
            Method method = AbstractClientConnector.class.getDeclaredMethod(
                    "isThis", Object.class);
            int modifiers = method.getModifiers();
            if (Modifier.isFinal(modifiers) || !Modifier.isProtected(modifiers)
                    || Modifier.isStatic(modifiers)) {
                Assert.fail("isThis has invalid modifiers, CDI proxies will not work.");
            }
        } catch (SecurityException e) {
            // Ignore, no can do
        } catch (NoSuchMethodException e) {
            Assert.fail("isThis is missing, CDI proxies will not work.");
        }
    }

}
