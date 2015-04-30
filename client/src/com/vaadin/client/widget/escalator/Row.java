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

import com.google.gwt.dom.client.TableRowElement;

/**
 * A representation of a row in an {@link Escalator}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 */
public interface Row {
    /**
     * Gets the row index.
     * 
     * @return the row index
     */
    public int getRow();

    /**
     * Gets the root element for this row.
     * <p>
     * The {@link EscalatorUpdater} may update the class names of the element
     * and add inline styles, but may not modify the contained DOM structure.
     * <p>
     * If you wish to modify the cells within this row element, access them via
     * the <code>List&lt;{@link Cell}&gt;</code> objects passed in to
     * {@code EscalatorUpdater.updateCells(Row, List)}
     * 
     * @return the root element of the row
     */
    public TableRowElement getElement();
}