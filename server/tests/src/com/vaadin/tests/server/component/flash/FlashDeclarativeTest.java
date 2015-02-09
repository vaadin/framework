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
package com.vaadin.tests.server.component.flash;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.AbstractEmbedded;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Flash;

/**
 * Tests declarative support for implementations of {@link AbstractEmbedded} and
 * {@link Embedded}.
 * 
 * @since
 * @author Vaadin Ltd
 */
public class FlashDeclarativeTest extends DeclarativeTestBase<Flash> {

    protected Flash getExpectedResult() {
        Flash f = new Flash();
        f.setArchive("arch");
        f.setCodebase("foo");
        f.setCodetype("bar");
        f.setStandby("Please wait");
        f.setParameter("foo", "bar");
        f.setParameter("baz", "foo");
        return f;
    };

    protected String getDesign() {
        return "<v-flash standby='Please wait' archive='arch' codebase='foo' codetype='bar' >"
                + "  <parameter name='baz' value='foo' />\n" //
                + "  <parameter name='foo' value='bar' />\n" //
                + "</v-flash>"; //
    }

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
        testRead("<v-flash />", new Flash());
    }

}
