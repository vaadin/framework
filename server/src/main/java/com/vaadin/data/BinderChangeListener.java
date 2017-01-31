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
package com.vaadin.data;

/**
 * Listener interface for value change events fired from the fields bound to a
 * binder.
 * 
 * @author Vaadin Ltd
 * 
 * @see BinderChangeEvent
 * @see Binder#addBinderChangeListener(BinderChangeListener)
 * 
 * @since 8.0
 *
 * @param <BEAN>
 *            the bean type
 */
@FunctionalInterface
public interface BinderChangeListener<BEAN> {

    /**
     * Notifies the listener about field value change {@code event} in a binder.
     *
     * @param event
     *            a value change event, not null
     */
    public void onValueChange(BinderChangeEvent<BEAN, ?> event);
}
