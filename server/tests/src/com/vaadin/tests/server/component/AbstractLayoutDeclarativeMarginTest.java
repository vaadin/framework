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

import org.junit.Test;

import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.AbstractLayout;
import com.vaadin.ui.VerticalLayout;

public class AbstractLayoutDeclarativeMarginTest extends
        DeclarativeTestBase<AbstractLayout> {

    @Test
    public void testMarginInfo() {
        VerticalLayout vl = new VerticalLayout();

        String left = getMarginTag(true, false, false, false);
        MarginInfo leftInfo = getMarginInfo(true, false, false, false);

        String right = getMarginTag(false, true, false, false);
        MarginInfo rightInfo = getMarginInfo(false, true, false, false);

        String top = getMarginTag(false, false, true, false);
        MarginInfo topInfo = getMarginInfo(false, false, true, false);

        String bottom = getMarginTag(false, false, false, true);
        MarginInfo bottomInfo = getMarginInfo(false, false, false, true);

        String topLeft = getMarginTag(true, false, true, false);
        MarginInfo topLeftInfo = getMarginInfo(true, false, true, false);

        String topRight = getMarginTag(false, true, true, false);
        MarginInfo topRightInfo = getMarginInfo(false, true, true, false);

        String bottomLeft = getMarginTag(true, false, false, true);
        MarginInfo bottomLeftInfo = getMarginInfo(true, false, false, true);

        String bottomRight = getMarginTag(false, true, false, true);
        MarginInfo bottomRightInfo = getMarginInfo(false, true, false, true);

        testRW(vl, left, leftInfo);
        testRW(vl, right, rightInfo);
        testRW(vl, top, topInfo);
        testRW(vl, bottom, bottomInfo);

        testRW(vl, topLeft, topLeftInfo);
        testRW(vl, topRight, topRightInfo);
        testRW(vl, bottomLeft, bottomLeftInfo);
        testRW(vl, bottomRight, bottomRightInfo);

        // Test special case of all edges margin'ed
        testRW(vl, getMarginTag(true, true, true, true), new MarginInfo(true));
    }

    private void testRW(VerticalLayout vl, String design, MarginInfo margin) {
        vl.setMargin(margin);
        testWrite(design, vl);
        testRead(design, vl);
    }

    private String getMarginTag(boolean left, boolean right, boolean top,
            boolean bottom) {
        String s = "<v-vertical-layout ";

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

    private MarginInfo getMarginInfo(boolean left, boolean right, boolean top,
            boolean bottom) {
        return new MarginInfo(top, right, bottom, left);
    }

}
