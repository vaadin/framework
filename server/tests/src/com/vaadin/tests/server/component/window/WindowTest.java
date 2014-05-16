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
package com.vaadin.tests.server.component.window;

import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import com.vaadin.ui.Button;
import com.vaadin.ui.Label;
import com.vaadin.ui.Window;

public class WindowTest {

    public Window window;

    @Before
    public void setup() {
        window = new Window();
    }

    @Test
    public void testAssistiveDescription() {
        Label l1 = new Label("label 1");
        Button b2 = new Button("button 2");
        window.setAssistiveDescription(l1, b2);

        Assert.assertEquals(2, window.getAssistiveDescription().length);
        Assert.assertEquals(l1, window.getAssistiveDescription()[0]);
        Assert.assertEquals(b2, window.getAssistiveDescription()[1]);

        // Modifying return value must not change actual value
        window.getAssistiveDescription()[0] = null;

        Assert.assertEquals(2, window.getAssistiveDescription().length);
        Assert.assertEquals(l1, window.getAssistiveDescription()[0]);
        Assert.assertEquals(b2, window.getAssistiveDescription()[1]);

    }
}
