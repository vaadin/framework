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
package com.vaadin.tests.server.component.abstractcomponent;

import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.HasComponents;

public class AbstractComponentSetParentTest {

    private static class TestComponent extends AbstractComponent {
    }

    @Test
    public void setParent_marks_old_parent_as_dirty() {
        HasComponents hasComponents = Mockito.mock(HasComponents.class);
        TestComponent testComponent = new TestComponent();
        testComponent.setParent(hasComponents);
        testComponent.setParent(null);
        Mockito.verify(hasComponents, Mockito.times(1)).markAsDirty();
    }
}
