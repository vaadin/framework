/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.client.ui;

import com.google.gwt.dom.client.Element;

/**
 * Implemented by widgets supporting an error indicator.
 *
 * @since 8.2
 */
public interface HasErrorIndicatorElement {

    /**
     * Gets the error indicator element.
     *
     * @return the error indicator element
     */
    Element getErrorIndicatorElement();

    /**
     * Sets the visibility of the error indicator element.
     *
     * @param visible
     *            {@code true} to show the error indicator element,
     *            {@code false} to hide it
     */
    void setErrorIndicatorElementVisible(boolean visible);
}
