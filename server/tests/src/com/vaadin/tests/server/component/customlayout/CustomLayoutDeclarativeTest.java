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
package com.vaadin.tests.server.component.customlayout;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Button;
import com.vaadin.ui.CustomLayout;
import com.vaadin.ui.Label;

/**
 * Tests declarative support for {@link CustomLayout}.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class CustomLayoutDeclarativeTest extends
        DeclarativeTestBase<CustomLayout> {

    @Test
    public void testEmpty() {
        String design = "<v-custom-layout>";
        CustomLayout expected = new CustomLayout();
        test(design, expected);
    }

    @Test
    public void testWithChildren() {
        String design = "<v-custom-layout>" + //
                "<v-button plain-text :location='b'></v-button>" + //
                "<v-label plain-text :location='l'></v-label>" + //
                "</v-custom-layout>";

        CustomLayout expected = new CustomLayout();
        expected.addComponent(new Button(), "b");
        expected.addComponent(new Label(), "l");

        test(design, expected);
    }

    @Test
    public void testWithOneChild() {
        String design = "<v-custom-layout><v-button plain-text></v-button></v-custom-layout>";

        CustomLayout expected = new CustomLayout();
        expected.addComponent(new Button());

        test(design, expected);
    }

    @Test
    public void testWithTemplate() {
        String design = "<v-custom-layout template-name='template.html'></v-custom-layout>";
        CustomLayout expected = new CustomLayout("template.html");
        test(design, expected);
    }

    @Test
    public void testWithDuplicateLocations() {
        String design = "<v-custom-layout>" + //
                "<v-button plain-text :location='foo'></v-button>" + //
                "<v-label plain-text :location='foo'></v-label>" + //
                "</v-custom-layout>";

        CustomLayout expected = new CustomLayout();
        expected.addComponent(new Button(), "foo");
        expected.addComponent(new Label(), "foo");

        testRead(design, expected);

        String written = "<v-custom-layout>" + //
                "<v-label plain-text :location='foo'></v-label>" + //
                "</v-custom-layout>";

        testWrite(written, expected);
    }

    protected void test(String design, CustomLayout expected) {
        testRead(design, expected);
        testWrite(design, expected);
    }
}
