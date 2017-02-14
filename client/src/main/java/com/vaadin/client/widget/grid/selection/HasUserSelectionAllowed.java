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
package com.vaadin.client.widget.grid.selection;

/**
 * Interface implemented by selection models which support disabling client side
 * selection while still allowing programmatic selection on the server.
 *
 * @param <T>
 *            Grid's row type
 * 
 * @since 7.7.7
 */
public interface HasUserSelectionAllowed<T> extends SelectionModel<T> {

    /**
     * Checks if the user is allowed to change the selection.
     * 
     * @return <code>true</code> if the user is allowed to change the selection,
     *         <code>false</code> otherwise
     */
    public boolean isUserSelectionAllowed();

    /**
     * Sets whether the user is allowed to change the selection.
     * 
     * @param userSelectionAllowed
     *            <code>true</code> if the user is allowed to change the
     *            selection, <code>false</code> otherwise
     */
    public void setUserSelectionAllowed(boolean userSelectionAllowed);

}
