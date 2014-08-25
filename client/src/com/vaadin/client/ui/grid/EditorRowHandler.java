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
package com.vaadin.client.ui.grid;

import com.google.gwt.user.client.ui.Widget;

/**
 * An interface for binding widgets and data to the editor row.
 * 
 * @since
 * @author Vaadin Ltd
 */
public interface EditorRowHandler<T> {

    /**
     * Returns the widget instance that is used to edit the values in the given
     * column. A null return value means the column is not editable.
     *
     * @param column
     *            the column whose values should be edited
     * @return the editor widget for the column or null if the column is not
     *         editable
     */
    public Widget getWidget(GridColumn<?, T> column);
}
