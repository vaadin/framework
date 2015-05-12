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
package com.vaadin.tests.server.component.nativeselect;

import org.junit.Test;

import com.vaadin.tests.design.DeclarativeTestBase;
import com.vaadin.ui.NativeSelect;

/**
 * Test cases for reading the properties of selection components.
 * 
 * @author Vaadin Ltd
 */
public class NativeSelectDeclarativeTest extends
        DeclarativeTestBase<NativeSelect> {

    public String getBasicDesign() {
        return "<v-native-select><option>foo</option><option>bar</option></v-native-select>";

    }

    public NativeSelect getBasicExpected() {
        NativeSelect ns = new NativeSelect();
        ns.addItem("foo");
        ns.addItem("bar");
        return ns;
    }

    @Test
    public void testReadBasic() {
        testRead(getBasicDesign(), getBasicExpected());
    }

    @Test
    public void testWriteBasic() {
        testWrite(stripOptionTags(getBasicDesign()), getBasicExpected());
    }

}