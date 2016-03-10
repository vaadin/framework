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

import com.vaadin.ui.HasComponents.ComponentAttachDetachNotifier;

/**
 * Interface for component containers that have one child component and do not
 * support adding or removing components.
 * 
 * For component containers that support multiple children, see
 * {@link ComponentContainer} instead.
 * 
 * @since 7.0
 */
public interface SingleComponentContainer extends HasComponents,
        ComponentAttachDetachNotifier {

    /**
     * Gets the number of children this {@link SingleComponentContainer} has.
     * This must be symmetric with what {@link #iterator()} returns and thus
     * typically return 1 if the content is set, 0 otherwise.
     * 
     * @return The number of child components this container has.
     */
    public int getComponentCount();

    /**
     * Gets the content of this container. The content is a component that
     * serves as the outermost item of the visual contents.
     * 
     * @return a component to use as content
     * 
     * @see #setContent(Component)
     */
    public Component getContent();

    /**
     * Sets the content of this container. The content is a component that
     * serves as the outermost item of the visual contents.
     * 
     * The content should always be set, either as a constructor parameter or by
     * calling this method.
     * 
     * @return a component (typically a layout) to use as content
     */
    public void setContent(Component content);

}
