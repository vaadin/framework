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
package com.vaadin.tests.proto;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Random;

import com.vaadin.server.VaadinRequest;
import com.vaadin.tests.components.AbstractTestUI;
import com.vaadin.tests.fieldgroup.ComplexPerson;
import com.vaadin.ui.proto.TypedGrid;

public class TypedGridUI extends AbstractTestUI {

    @Override
    protected void setup(VaadinRequest request) {
        addComponent(new TypedGrid<ComplexPerson>(ComplexPerson.class,
                createPersons(100)));
    }

    private static Collection<ComplexPerson> createPersons(int count) {
        Random r = new Random(1337);
        List<ComplexPerson> list = new ArrayList<ComplexPerson>();
        for (int i = 0; i < count; ++i) {
            list.add(ComplexPerson.create(r));
        }
        return list;
    }

}
