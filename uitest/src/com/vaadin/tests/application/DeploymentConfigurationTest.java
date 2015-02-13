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
package com.vaadin.tests.application;

import static org.junit.Assert.assertTrue;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.junit.Test;

import com.vaadin.testbench.elements.LabelElement;
import com.vaadin.tests.tb3.MultiBrowserTest;

public class DeploymentConfigurationTest extends MultiBrowserTest {

    @Test
    public void testParameters() {
        openTestURL();
        List<String> texts = new ArrayList<String>(Arrays.asList(
                "Init parameters:", "legacyPropertyToString: false",
                "closeIdleSessions: true", "productionMode: false",
                "testParam: 42", "heartbeatInterval: 301",
                "resourceCacheTime: 3601"));

        for (LabelElement label : $(LabelElement.class).all()) {
            assertTrue(label.getText() + " not found",
                    texts.contains(label.getText()));
            texts.remove(label.getText());
        }
        assertTrue(texts.isEmpty());
    }

}
