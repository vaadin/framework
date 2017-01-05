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

package com.vaadin.server;

import java.io.Serializable;
import java.util.Map;

import com.vaadin.ui.LegacyComponent;

/**
 * <p>
 * Listener interface for UI variable changes. The user communicates with the
 * application using the so-called <i>variables</i>. When the user makes a
 * change using the UI the terminal trasmits the changed variables to the
 * application, and the components owning those variables may then process those
 * changes.
 * </p>
 *
 * @author Vaadin Ltd.
 * @since 3.0
 * @deprecated As of 7.0. Only provided to ease porting of Vaadin 6 components.
 *             Do not implement this directly, implement {@link LegacyComponent}
 *             .
 */
@Deprecated
public interface VariableOwner extends Serializable {

    /**
     * Called when one or more variables handled by the implementing class are
     * changed.
     *
     * @param source
     *            the Source of the variable change. This is the origin of the
     *            event. For example in Web Adapter this is the request.
     * @param variables
     *            the Mapping from variable names to new variable values.
     */
    public void changeVariables(Object source, Map<String, Object> variables);

    /**
     * <p>
     * Tests if the variable owner is enabled or not. The terminal should not
     * send any variable changes to disabled variable owners.
     * </p>
     *
     * @return <code>true</code> if the variable owner is enabled,
     *         <code>false</code> if not
     */
    public boolean isEnabled();

}
