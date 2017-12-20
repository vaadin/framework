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
package com.vaadin.tests.design.nested;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;

import org.junit.Before;
import org.junit.Test;

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
        assertEquals(1, root.getComponentCount());
        assertThat(root.iterator().next(),
                instanceOf(MyExtendedChildDesign.class));
    }

    @Test
    public void rootContainsTwoGrandChildren() {
        assertEquals(2, root.childDesign.getComponentCount());
    }

    @Test
    public void childComponentIsNotNull() {
        assertNotNull(root.childDesign);
    }

    @Test
    public void childLabelIsNotNull() {
        assertNotNull(root.childDesign.childLabel);
        assertEquals("test content", root.childDesign.childLabel.getValue());
    }

    @Test
    public void childCustomComponentsIsNotNull() {
        assertNotNull(root.childDesign.childCustomComponent);
        assertEquals("custom content",
                root.childDesign.childCustomComponent.getCaption());
    }
}
