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
package com.vaadin.tests.server.component;

import org.junit.Assert;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.Layout;
import com.vaadin.ui.Layout.MarginHandler;

public abstract class DeclarativeMarginTestBase<L extends Layout & MarginHandler>
        extends DeclarativeTestBase<L> {

    protected void testMargins(String componentTag) {

        for (int i = 0; i < 16; ++i) {
            boolean top = (i & 1) == 1;
            boolean right = (i & 2) == 2;
            boolean bottom = (i & 4) == 4;
            boolean left = (i & 8) == 8;

            MarginInfo m = new MarginInfo(top, right, bottom, left);

            String design = getMarginTag(componentTag, top, right, bottom, left);

            // The assertEquals machinery in DeclarativeTestBase uses bean
            // introspection and MarginInfo is not a proper bean. It ends up
            // considering *all* MarginInfo objects equal... (#18229)
            L layout = read(design);
            Assert.assertEquals(m, layout.getMargin());

            testWrite(design, layout);
        }
    }

    private String getMarginTag(String componentTag, boolean top,
            boolean right, boolean bottom, boolean left) {
        String s = "<" + componentTag + " ";

        if (left && right && top && bottom) {
            s += "margin='true'";
        } else {
            if (left) {
                s += "margin-left='true' ";
            }
            if (right) {
                s += "margin-right='true' ";
            }
            if (top) {
                s += "margin-top='true' ";
            }
            if (bottom) {
                s += "margin-bottom='true' ";
            }
        }
        return s + " />";
    }
}
