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

package com.vaadin.v7.data.util.converter;

import com.vaadin.data.Binder;

import java.io.Serializable;

/**
 * Factory interface for providing Converters based on a presentation type and a
 * model type.
 *
 * @author Vaadin Ltd.
 * @since 7.0
 *
 * @deprecated As of 8.0, no replacement available - provide explicit converters for {@link Binder}.
 */
@Deprecated
public interface ConverterFactory extends Serializable {
    public <PRESENTATION, MODEL> Converter<PRESENTATION, MODEL> createConverter(
            Class<PRESENTATION> presentationType, Class<MODEL> modelType);

}
