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

import com.vaadin.shared.ui.AbstractLayoutState;

/**
 * An abstract class that defines default implementation for the {@link Layout}
 * interface.
 * 
 * @author Vaadin Ltd.
 * @since 5.0
 */
public abstract class AbstractLayout extends AbstractComponentContainer
        implements Layout {

    @Override
    protected AbstractLayoutState getState() {
        return (AbstractLayoutState) super.getState();
    }

}
