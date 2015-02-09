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
package com.vaadin.tests.server.component.browserframe;

import org.junit.Test;

import com.vaadin.server.ExternalResource;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.BrowserFrame;

/**
 * Tests declarative support for implementations of {@link BrowserFrame}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public class BrowserFrameDeclarativeTest extends
        DeclarativeTestBase<BrowserFrame> {

    protected String getDesign() {
        return "<v-browser-frame source='http://foo.bar/some.html' />";
    }

    protected BrowserFrame getExpectedResult() {
        BrowserFrame i = new BrowserFrame();
        i.setSource(new ExternalResource("http://foo.bar/some.html"));
        return i;
    };

    @Test
    public void read() {
        testRead(getDesign(), getExpectedResult());
    }

    @Test
    public void write() {
        testWrite(getDesign(), getExpectedResult());
    }

    @Test
    public void testEmpty() {
        testRead("<v-browser-frame/>", new BrowserFrame());
    }
}
