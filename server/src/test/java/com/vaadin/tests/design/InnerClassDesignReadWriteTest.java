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
package com.vaadin.tests.design;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;

import org.junit.Test;

import com.vaadin.tests.design.InnerClassDesignReadWriteTest.Foo.StaticInnerInner;
import com.vaadin.tests.design.UPPERCASE.InUpperCasePackage;
import com.vaadin.ui.GridLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.declarative.Design;

public class InnerClassDesignReadWriteTest {
    @Test
    public void testWritingAndReadingBackInnerClass() throws IOException {
        VerticalLayout vl = new VerticalLayout();
        vl.addComponent(new StaticInner());
        vl.addComponent(new StaticInnerInner());
        vl.addComponent(new InUpperCasePackage());

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        Design.write(vl, baos);
        Design.read(new ByteArrayInputStream(baos.toByteArray()));
    }

    public static class StaticInner extends GridLayout {
    }

    public static class Foo {
        public static class StaticInnerInner extends HorizontalLayout {

        }
    }

}
