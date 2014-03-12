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
package com.vaadin.tests.server.component.abstractcomponentcontainer;

import java.util.Iterator;

import org.easymock.EasyMock;
import org.junit.Test;

import com.vaadin.ui.AbstractComponentContainer;
import com.vaadin.ui.Component;
import com.vaadin.ui.HasComponents;

/**
 * Tests for avoiding add parent as child for
 * {@link AbstractComponentContainer#addComponent(Component)}
 * 
 * @author Vaadin Ltd
 */
public class AddParentAsChild {

    @Test(expected = IllegalArgumentException.class)
    public void testAddComponent() {
        AbstractComponentContainer container = new ComponentContainer();
        HasComponents hasComponentsMock = EasyMock
                .createMock(HasComponents.class);
        container.setParent(hasComponentsMock);

        container.addComponent(hasComponentsMock);
    }

    class ComponentContainer extends AbstractComponentContainer {

        @Override
        public void replaceComponent(Component oldComponent,
                Component newComponent) {
        }

        @Override
        public int getComponentCount() {
            return 0;
        }

        @Override
        public Iterator<Component> iterator() {
            return null;
        }

    }

}
