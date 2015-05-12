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
package com.vaadin.tests.server.component.link;

import org.junit.Test;

import com.vaadin.server.ExternalResource;
import com.vaadin.shared.ui.BorderStyle;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Link;

/**
 * Test cases for reading the properties of selection components.
 * 
 * @author Vaadin Ltd
 */
public class LinkDeclarativeTest extends DeclarativeTestBase<Link> {
    private String getBasicDesign() {
        return "<v-link href='http://vaadin.com' target='vaadin-window' target-height=500"
                + " target-width=800 target-border='none' />";
    }

    private Link getBasicExpected() {
        Link l = new Link();
        l.setResource(new ExternalResource("http://vaadin.com"));
        l.setTargetName("vaadin-window");
        l.setTargetBorder(BorderStyle.NONE);
        l.setTargetHeight(500);
        l.setTargetWidth(800);
        return l;
    }

    @Test
    public void readBasic() throws Exception {
        testRead(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void writeBasic() throws Exception {
        testWrite(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void testReadEmpty() {
        testRead("<v-link />", new Link());
    }

    @Test
    public void testWriteEmpty() {
        testWrite("<v-link />", new Link());
    }

}