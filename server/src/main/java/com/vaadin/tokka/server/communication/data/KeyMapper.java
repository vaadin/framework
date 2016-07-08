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
package com.vaadin.tokka.server.communication.data;

/**
 * Generic {@link DataKeyMapper} implementation based on
 * {@link com.vaadin.server.KeyMapper}. Provides the interface on top of super
 * class.
 * 
 * @since
 * @param <T>
 *            data type
 */
public class KeyMapper<T> extends com.vaadin.server.KeyMapper<T> implements
        DataKeyMapper<T> {
}