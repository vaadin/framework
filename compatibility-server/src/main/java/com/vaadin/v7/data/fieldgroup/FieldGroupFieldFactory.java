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
package com.vaadin.v7.data.fieldgroup;

import java.io.Serializable;

import com.vaadin.data.Binder;
import com.vaadin.data.HasValue;
import com.vaadin.v7.ui.Field;

/**
 * Factory interface for creating new Field-instances based on the data type
 * that should be edited.
 *
 * @author Vaadin Ltd.
 * @since 7.0
 * @deprecated As of 8.0, no direct replacement available. {@link Binder#forMemberField(HasValue)} and
 * {@link Binder#bindInstanceFields(Object)} should be used instead.
 */
@Deprecated
public interface FieldGroupFieldFactory extends Serializable {
    /**
     * Creates a field based on the data type that we want to edit
     *
     * @param dataType
     *            The type that we want to edit using the field
     * @param fieldType
     *            The type of field we want to create. If set to {@link Field}
     *            then any type of field is accepted
     * @return A field that can be assigned to the given fieldType and that is
     *         capable of editing the given type of data
     */
    <T extends Field> T createField(Class<?> dataType, Class<T> fieldType);
}
