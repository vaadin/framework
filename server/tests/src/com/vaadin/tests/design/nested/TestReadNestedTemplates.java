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

import junit.framework.TestCase;

/**
 * Test case for reading nested templates
 * 
 * @since
 * @author Vaadin Ltd
 */
public class TestReadNestedTemplates extends TestCase {

    private MyDesignRoot root;

    @Override
    protected void setUp() throws Exception {
        super.setUp();
        root = new MyDesignRoot();
    }

    public void testChildren() {
        assertEquals("The root layout must contain one child", 1,
                root.getComponentCount());
        assertTrue(root.iterator().next() instanceof MyExtendedChildDesign);
    }

    public void testGrandChildren() {
        assertEquals("The root layout must have two grandchildren", 2,
                root.childDesign.getComponentCount());
    }

    public void testRootComponentFields() {
        assertNotNull("The child component must not be null", root.childDesign);
    }

    public void testChildComponentFields() {
        assertNotNull("Grandchildren must not be null",
                root.childDesign.childLabel);
        assertNotNull("Grandchildren must not be null",
                root.childDesign.childCustomComponent);
        assertEquals("child label caption must be read", "test content",
                root.childDesign.childLabel.getValue());
        assertEquals("child custom component caption must be read",
                "custom content",
                root.childDesign.childCustomComponent.getCaption());
    }
}
