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
package com.vaadin.tests.server.component.listselect;

import org.junit.Test;

import com.vaadin.tests.server.component.abstractmultiselect.AbstractMultiSelectDeclarativeTest;
import com.vaadin.ui.ListSelect;

/**
 * List select declarative test.
 * <p>
 * There is only {@link ListSelect#setRows(int)}/{@link ListSelect#getRows()}
 * explicit test. All other tests are in the super class (
 * {@link AbstractMultiSelectDeclarativeTest}).
 *
 * @see AbstractMultiSelectDeclarativeTest
 *
 * @author Vaadin Ltd
 *
 */
@SuppressWarnings("rawtypes")
public class ListSelectDeclarativeTest
        extends AbstractMultiSelectDeclarativeTest<ListSelect> {

    @Test
    public void rowsPropertySerialization() {
        int rows = 7;
        String design = String.format("<%s rows='%s'/>", getComponentTag(),
                rows);

        ListSelect<String> select = new ListSelect<>();
        select.setRows(rows);

        testRead(design, select);
        testWrite(design, select);
    }

    @Override
    protected String getComponentTag() {
        return "vaadin-list-select";
    }

    @Override
    protected Class<? extends ListSelect> getComponentClass() {
        return ListSelect.class;
    }

}
