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
package com.vaadin.tests.server.abstractextension;

import org.junit.Test;
import org.mockito.Mockito;

import com.vaadin.server.AbstractExtension;
import com.vaadin.server.ClientConnector;

public class AbstractExtensionSetParentTest {

    private static class TestExtension extends AbstractExtension {

    }

    @Test
    public void setParent_marks_old_parent_as_dirty() {
        ClientConnector connector = Mockito.mock(ClientConnector.class);
        TestExtension extension = new TestExtension();
        extension.setParent(connector);
        extension.setParent(null);
        Mockito.verify(connector, Mockito.times(1)).markAsDirty();
    }
}
