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
package com.vaadin.ui.declarative;

import java.io.Serializable;

import com.vaadin.ui.Component;

/**
 * Delegate used by {@link DesignContext} to determine whether container data
 * should be written out for a component.
 * 
 * @see DesignContext#shouldWriteData(Component)
 * 
 * @since 7.5.0
 * @author Vaadin Ltd
 */
public interface ShouldWriteDataDelegate extends Serializable {

    /**
     * The default delegate implementation that assumes that all component data
     * is provided by a data source connected to a back end system and that the
     * data should thus not be written.
     */
    public static final ShouldWriteDataDelegate DEFAULT = new ShouldWriteDataDelegate() {
        @Override
        public boolean shouldWriteData(Component component) {
            return false;
        }
    };

    /**
     * Determines whether the container data of a component should be written
     * out.
     * 
     * @param component
     *            the component to check
     * @return <code>true</code> if container data should be written out for the
     *         provided component; otherwise <code>false</code>.
     */
    boolean shouldWriteData(Component component);
}
