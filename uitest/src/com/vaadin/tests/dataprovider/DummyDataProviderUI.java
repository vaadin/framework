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
package com.vaadin.tests.dataprovider;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.Random;

import com.vaadin.annotations.Widgetset;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.communication.data.typed.DataProvider;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.fieldgroup.ComplexPerson;
import com.vaadin.tests.widgetset.TestingWidgetSet;
import com.vaadin.ui.AbstractComponent;

@Widgetset(TestingWidgetSet.NAME)
public class DummyDataProviderUI extends AbstractTestUI {

    public static class DummyDataComponent<T> extends AbstractComponent {

        public DummyDataComponent(Collection<T> data) {
            new DataProvider<T>(data, this);
        }

    }

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new DummyDataComponent<ComplexPerson>(getPersons(20)));
    }

    private Collection<ComplexPerson> getPersons(int count) {
        Random r = new Random(1337);
        Collection<ComplexPerson> c = new LinkedHashSet<ComplexPerson>();
        for (int i = 0; i < count; ++i) {
            c.add(ComplexPerson.create(r));
        }
        return c;
    }
}
