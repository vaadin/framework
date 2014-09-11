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

import java.util.Collections;
import java.util.Iterator;

/**
 * Custom component provides a simple implementation of the {@link Component}
 * interface to allow creating new UI components by composition of existing
 * server-side components.
 * 
 * <p>
 * The component is used by inheriting the CustomComponent class and setting the
 * composition root component. The composition root must be set with
 * {@link #setCompositionRoot(Component)} before the CustomComponent is used,
 * such as by adding it to a layout, so it is preferable to set it in the
 * constructor.
 * </p>
 * 
 * <p>
 * The composition root itself can contain more components. The advantage of
 * wrapping it in a CustomComponent is that its details, such as interfaces, are
 * hidden from the users of the component, thereby contributing to information
 * hiding.
 * </p>
 * 
 * <p>
 * The CustomComponent does not display the caption of the composition root, so
 * if you want to have it shown in the layout where the custom component is
 * contained, you need to set it as caption of the CustomComponent.
 * </p>
 * 
 * <p>
 * The component expands horizontally and has undefined height by default.
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
     * Note that you must set the composition root before the component can be
     * used, preferably in the constructor.
     * </p>
     */
    public CustomComponent() {
        // Expand horizontally by default
        setWidth(100, Unit.PERCENTAGE);
    }

    /**
     * Constructs a new custom component.
     * 
     * @param compositionRoot
     *            the root of the composition component tree. It must not be
     *            null.
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
     * Sets the composition root for the component.
     * 
     * <p>
     * You must set the composition root must to a non-null value before the
     * component can be used. You can change it later.
     * </p>
     * 
     * @param compositionRoot
     *            the root of the composition component tree.
     */
    protected void setCompositionRoot(Component compositionRoot) {
        if (compositionRoot != root) {
            if (root != null && equals(root.getParent())) {
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
