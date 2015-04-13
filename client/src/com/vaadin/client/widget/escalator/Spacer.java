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
package com.vaadin.client.widget.escalator;

import com.google.gwt.dom.client.Element;

/**
 * A representation of a spacer element in a
 * {@link com.vaadin.client.widget.escalator.RowContainer.BodyRowContainer}.
 * 
 * @since 7.5.0
 * @author Vaadin Ltd
 */
public interface Spacer {

    /**
     * Gets the root element for the spacer content.
     * 
     * @return the root element for the spacer content
     */
    Element getElement();

    /**
     * Gets the decorative element for this spacer.
     */
    Element getDecoElement();

    /**
     * Gets the row index.
     * 
     * @return the row index.
     */
    int getRow();
}
