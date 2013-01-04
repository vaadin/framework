/*
 * Copyright 2000-2013 Vaadin Ltd.
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

import java.util.Collections;
import java.util.Iterator;

/**
 * Custom component provides simple implementation of Component interface for
 * creation of new UI components by composition of existing components.
 * <p>
 * The component is used by inheriting the CustomComponent class and setting
 * composite root inside the Custom component. The composite root itself can
 * contain more components, but their interfaces are hidden from the users.
 * </p>
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class CustomComponent extends AbstractComponent implements HasComponents {

    /**
     * The root component implementing the custom component.
     */
    private Component root = null;

    /**
     * Constructs a new custom component.
     * 
     * <p>
     * The component is implemented by wrapping the methods of the composition
     * root component given as parameter. The composition root must be set
     * before the component can be used.
     * </p>
     */
    public CustomComponent() {
        // expand horizontally by default
        setWidth(100, UNITS_PERCENTAGE);
    }

    /**
     * Constructs a new custom component.
     * 
     * <p>
     * The component is implemented by wrapping the methods of the composition
     * root component given as parameter. The composition root must not be null
     * and can not be changed after the composition.
     * </p>
     * 
     * @param compositionRoot
     *            the root of the composition component tree.
     */
    public CustomComponent(Component compositionRoot) {
        this();
        setCompositionRoot(compositionRoot);
    }

    /**
     * Returns the composition root.
     * 
     * @return the Component Composition root.
     */
    protected Component getCompositionRoot() {
        return root;
    }

    /**
     * Sets the compositions root.
     * <p>
     * The composition root must be set to non-null value before the component
     * can be used. The composition root can only be set once.
     * </p>
     * 
     * @param compositionRoot
     *            the root of the composition component tree.
     */
    protected void setCompositionRoot(Component compositionRoot) {
        if (compositionRoot != root) {
            if (root != null && root.getParent() == this) {
                // remove old component
                root.setParent(null);
            }
            if (compositionRoot != null) {
                // set new component
                if (compositionRoot.getParent() != null) {
                    // If the component already has a parent, try to remove it
                    AbstractSingleComponentContainer
                            .removeFromParent(compositionRoot);
                }
                compositionRoot.setParent(this);
            }
            root = compositionRoot;
            markAsDirty();
        }
    }

    /* Basic component features ------------------------------------------ */

    @Override
    public Iterator<Component> iterator() {
        if (getCompositionRoot() != null) {
            return Collections.singletonList(getCompositionRoot()).iterator();
        } else {
            return Collections.<Component> emptyList().iterator();
        }
    }

    /**
     * Gets the number of contained components. Consistent with the iterator
     * returned by {@link #getComponentIterator()}.
     * 
     * @return the number of contained components (zero or one)
     */
    public int getComponentCount() {
        return (root != null ? 1 : 0);
    }

}
