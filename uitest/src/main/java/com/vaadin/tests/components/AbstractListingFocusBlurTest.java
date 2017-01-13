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
package com.vaadin.tests.components;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import com.googlecode.gentyref.GenericTypeReflector;
import com.vaadin.event.FieldEvents.BlurNotifier;
import com.vaadin.event.FieldEvents.FocusNotifier;
import com.vaadin.server.VaadinRequest;
import com.vaadin.ui.AbstractListing;

/**
 * @author Vaadin Ltd
 *
 */
public abstract class AbstractListingFocusBlurTest<T extends AbstractListing<Integer> & FocusNotifier & BlurNotifier>
        extends AbstractTestUIWithLog {

    @Override
    @SuppressWarnings("unchecked")
    protected void setup(VaadinRequest request) {
        Type valueType = GenericTypeReflector.getTypeParameter(getClass(),
                AbstractListingFocusBlurTest.class.getTypeParameters()[0]);
        if (valueType instanceof ParameterizedType) {
            valueType = ((ParameterizedType) valueType).getRawType();
        }
        if (valueType instanceof Class<?>) {
            Class<T> clazz = (Class<T>) valueType;
            try {
                T select = clazz.newInstance();
                select.setItems(
                        IntStream.range(1, 10).mapToObj(Integer::valueOf)
                                .collect(Collectors.toList()));

                addComponent(select);
                select.addFocusListener(event -> log("Focus Event"));
                select.addBlurListener(event -> log("Blur Event"));
            } catch (InstantiationException | IllegalAccessException e) {
                throw new RuntimeException(e);
            }
        } else {
            throw new RuntimeException(
                    "Unexpected component type " + valueType.getTypeName());
        }
    }

}
