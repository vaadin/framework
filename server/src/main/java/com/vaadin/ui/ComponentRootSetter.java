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
package com.vaadin.ui;

import java.io.Serializable;

/**
 * Internal utility class.
 *
 * @since 8.1
 * @author Vaadin Ltd
 */
public class ComponentRootSetter implements Serializable {

    private ComponentRootSetter() {
        // Util methods only
    }

    /**
     * Sets the composition root for the given custom component or composite.
     * <p>
     * For internal use only.
     *
     * @param customComponent
     *            the custom component or composite
     * @param component
     *            the component to assign as composition root
     */
    public static void setRoot(Component customComponent, Component component) {
        if (customComponent instanceof CustomComponent) {
            ((CustomComponent) customComponent).setCompositionRoot(component);
        } else if (customComponent instanceof Composite) {
            ((Composite) customComponent).setCompositionRoot(component);
        } else {
            throw new IllegalArgumentException(
                    "Parameter is of an unsupported type: "
                            + customComponent.getClass().getName());
        }
    }

}
