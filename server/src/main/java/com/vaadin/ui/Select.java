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

package com.vaadin.ui;

import java.util.Collection;

import com.vaadin.data.Container;

/**
 * <p>
 * A class representing a selection of items the user has selected in a UI. The
 * set of choices is presented as a set of {@link com.vaadin.data.Item}s in a
 * {@link com.vaadin.data.Container}.
 * </p>
 * 
 * <p>
 * A <code>Select</code> component may be in single- or multiselect mode.
 * Multiselect mode means that more than one item can be selected
 * simultaneously.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 * @deprecated As of 7.0. Use {@link ComboBox} instead.
 */
@Deprecated
public class Select extends ComboBox {
    /* Component methods */

    public Select() {
        super();
    }

    public Select(String caption, Collection<?> options) {
        super(caption, options);
    }

    public Select(String caption, Container dataSource) {
        super(caption, dataSource);
    }

    public Select(String caption) {
        super(caption);
    }

}
