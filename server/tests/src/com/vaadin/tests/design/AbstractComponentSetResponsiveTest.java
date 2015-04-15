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
package com.vaadin.tests.design;

import org.junit.Test;

import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Label;

public class AbstractComponentSetResponsiveTest extends
        DeclarativeTestBase<Label> {

    @Test
    public void testResponsiveFlag() {
        Label label = new Label();
        label.setContentMode(ContentMode.HTML);
        label.setResponsive(true);

        String design = "<v-label responsive='true' />";

        testWrite(design, label);
        testRead(design, label);
    }

}
