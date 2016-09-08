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

import com.vaadin.ui.Component;

/**
 * Interface implemented by field which can be marked as required. A required
 * status is handled by the parent layout.
 *
 * @since 8.0
 * @author Vaadin Ltd
 */
public interface HasRequired extends Component {

    /**
     * Sets whether the field is required or not.
     *
     * If the field is required, it is visually indicated in the user interface.
     *
     * @param required
     *            <code>true</code> to make the field required,
     *            <code>false</code> otherwise
     */
    public void setRequired(boolean required);

    /**
     * Checks whether the field is required.
     *
     * @return <code>true</code> if the field is required, <code>false</code>
     *         otherwise
     */
    public boolean isRequired();

}
