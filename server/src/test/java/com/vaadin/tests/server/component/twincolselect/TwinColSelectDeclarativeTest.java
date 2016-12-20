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
package com.vaadin.tests.server.component.twincolselect;

import org.junit.Test;

import com.vaadin.tests.server.component.abstractmultiselect.AbstractMultiSelectDeclarativeTest;
import com.vaadin.ui.TwinColSelect;

/**
 * TwinColSelectt declarative test.
 * <p>
 * There are only TwinColSelect specific properties explicit tests. All other
 * tests are in the super class ( {@link AbstractMultiSelectDeclarativeTest}).
 *
 * @see AbstractMultiSelectDeclarativeTest
 *
 * @author Vaadin Ltd
 *
 */
public class TwinColSelectDeclarativeTest
        extends AbstractMultiSelectDeclarativeTest<TwinColSelect> {

    @Test
    public void rowsPropertySerialization() {
        int rows = 7;
        String design = String.format("<%s rows='%s'/>", getComponentTag(),
                rows);

        TwinColSelect<String> select = new TwinColSelect<>();
        select.setRows(rows);

        testRead(design, select);
        testWrite(design, select);
    }

    @Test
    public void rightColumnCaptionPropertySerialization() {
        String rightColumnCaption = "foo";
        String design = String.format("<%s right-column-caption='%s'/>",
                getComponentTag(), rightColumnCaption);

        TwinColSelect<String> select = new TwinColSelect<>();
        select.setRightColumnCaption(rightColumnCaption);

        testRead(design, select);
        testWrite(design, select);
    }

    @Test
    public void leftColumnCaptionPropertySerialization() {
        String leftColumnCaption = "foo";
        String design = String.format("<%s left-column-caption='%s'/>",
                getComponentTag(), leftColumnCaption);

        TwinColSelect<String> select = new TwinColSelect<>();
        select.setLeftColumnCaption(leftColumnCaption);

        testRead(design, select);
        testWrite(design, select);
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-twin-col-select";
    }

    @Override
    protected Class<? extends TwinColSelect> getComponentClass() {
        return TwinColSelect.class;
    }

}