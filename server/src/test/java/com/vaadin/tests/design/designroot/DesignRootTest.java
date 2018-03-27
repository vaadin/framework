/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.tests.design.designroot;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;

import org.junit.Test;

public class DesignRootTest {
    @Test
    public void designAnnotationWithoutFilename() {
        DesignWithEmptyAnnotation d = new DesignWithEmptyAnnotation();
        assertNotNull(d.ok);
        assertNotNull(d.CaNCEL);
        assertEquals("original", d.preInitializedField.getValue());
    }

    @Test
    public void designAnnotationWithFilename() {
        DesignWithAnnotation d = new DesignWithAnnotation();
        assertNotNull(d.ok);
        assertNotNull(d.cancel);
        assertEquals("original", d.preInitializedField.getValue());
    }

    @Test
    public void extendedDesignAnnotationWithoutFilename() {
        DesignWithEmptyAnnotation d = new ExtendedDesignWithEmptyAnnotation();
        assertNotNull(d.ok);
        assertNotNull(d.CaNCEL);
        assertEquals("original", d.preInitializedField.getValue());
    }

    @Test
    public void extendedDesignAnnotationWithFilename() {
        DesignWithAnnotation d = new ExtendedDesignWithAnnotation();
        assertNotNull(d.ok);
        assertNotNull(d.cancel);
        assertEquals("original", d.preInitializedField.getValue());
    }

}
