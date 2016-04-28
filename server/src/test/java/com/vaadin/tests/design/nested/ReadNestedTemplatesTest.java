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
package com.vaadin.tests.design.nested;

import org.junit.Before;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.hamcrest.core.IsNot.not;
import static org.junit.Assert.*;

/**
 * Test case for reading nested templates
 * 
 * @since
 * @author Vaadin Ltd
 */
public class ReadNestedTemplatesTest {

    private MyDesignRoot root;

    @Before
    public void setUp() {
        root = new MyDesignRoot();
    }

    @Test
    public void rootContainsOneChild() {
        assertThat(root.getComponentCount(), is(1));
        assertThat(root.iterator().next(),
                instanceOf(MyExtendedChildDesign.class));
    }

    @Test
    public void rootContainsTwoGrandChildren() {
        assertThat(root.childDesign.getComponentCount(), is(2));
    }

    @Test
    public void childComponentIsNotNull() {
        assertThat(root.childDesign, is(not(nullValue())));
    }

    @Test
    public void childLabelIsNotNull() {
        assertThat(root.childDesign.childLabel, is(not(nullValue())));
        assertThat(root.childDesign.childLabel.getValue(), is("test content"));
    }

    @Test
    public void childCustomComponentsIsNotNull() {
        assertThat(root.childDesign.childCustomComponent, is(not(nullValue())));
        assertThat(root.childDesign.childCustomComponent.getCaption(),
                is("custom content"));
    }
}
