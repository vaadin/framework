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

import junit.framework.TestCase;

import org.junit.Assert;

import com.vaadin.server.Sizeable.Unit;
import com.vaadin.shared.ui.label.LabelState;
import com.vaadin.ui.Label;

public class ComponentSizeParseTest extends TestCase {

    private final class LabelWithPublicState extends Label {
        @Override
        protected LabelState getState() {
            return super.getState();
        }
    }

    public void testAllTheUnit() {
        testUnit("10.0px", 10, Unit.PIXELS);
        testUnit("10.0pt", 10, Unit.POINTS);
        testUnit("10.0pc", 10, Unit.PICAS);
        testUnit("10.0em", 10, Unit.EM);
        testUnit("10.0rem", 10, Unit.REM);
        testUnit("10.0mm", 10, Unit.MM);
        testUnit("10.0cm", 10, Unit.CM);
        testUnit("10.0in", 10, Unit.INCH);
        testUnit("10.0%", 10, Unit.PERCENTAGE);
    }

    private void testUnit(String string, int amout, Unit unit) {
        LabelWithPublicState label = new LabelWithPublicState();
        label.setHeight(string);

        Assert.assertEquals(amout, label.getHeight(), 0);
        Assert.assertEquals(unit, label.getHeightUnits());

        label = new LabelWithPublicState();

        label.setHeight(10, unit);
        label.beforeClientResponse(true);
        Assert.assertEquals(string, label.getState().height);
    }
}
