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
package com.vaadin.tests.server.component.abstractfield;

import org.junit.Test;

import com.vaadin.tests.server.component.abstractcomponent.AbstractComponentDeclarativeTestBase;
import com.vaadin.ui.AbstractField;

/**
 * Abstract test class which contains tests for declarative format for
 * properties that are common for AbstractField.
 * <p>
 * It's an abstract so it's not supposed to be run as is. Instead each
 * declarative test for a real component should extend it and implement abstract
 * methods to be able to test the common properties. Components specific
 * properties should be tested additionally in the subclasses implementations.
 *
 * @author Vaadin Ltd
 *
 */
public abstract class AbstractFieldDeclarativeTest<T extends AbstractField<V>, V>
        extends AbstractComponentDeclarativeTestBase<T> {

    @Test
    public void requiredDeserialization()
            throws InstantiationException, IllegalAccessException {
        boolean isRequired = true;
        String design = String.format("<%s required-indicator-visible/>",
                getComponentTag());

        T component = getComponentClass().newInstance();
        component.setRequiredIndicatorVisible(isRequired);
        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public void tabIndexDeserialization()
            throws InstantiationException, IllegalAccessException {
        int tabIndex = 13;
        String design = String.format("<%s tabindex='%s'/>", getComponentTag(),
                tabIndex);

        T component = getComponentClass().newInstance();
        component.setTabIndex(tabIndex);

        testRead(design, component);
        testWrite(design, component);
    }

    @Test
    public abstract void valueDeserialization()
            throws InstantiationException, IllegalAccessException;

    @Test
    public abstract void readOnlyValue()
            throws InstantiationException, IllegalAccessException;
}
