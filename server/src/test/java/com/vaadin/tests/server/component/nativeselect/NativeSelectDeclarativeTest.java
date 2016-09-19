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
package com.vaadin.tests.server.component.nativeselect;

import com.vaadin.tests.server.component.abstractsingleselect.AbstractSingleSelectDeclarativeTest;
import com.vaadin.ui.NativeSelect;

/**
 * Declarative support tests for {@link NativeSelect}. All tests are in the
 * super class ({@link AbstractSingleSelectDeclarativeTest}). This class
 * declares only tag name and native select class (test parameters).
 * 
 * @author Vaadin Ltd
 *
 */
public class NativeSelectDeclarativeTest
        extends AbstractSingleSelectDeclarativeTest<NativeSelect> {

    @Override
    protected String getComponentTag() {
        return "vaadin-native-select";
    }

    @Override
    protected Class<NativeSelect> getComponentClass() {
        return NativeSelect.class;
    }

}
