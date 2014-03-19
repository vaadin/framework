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

package com.vaadin.client.data;

/**
 * Callback interface used by {@link DataSource} to inform its user about
 * updates to the data.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public interface DataChangeHandler {
    /**
     * Called when the contents of the data source has changed. If the number of
     * rows has changed or if rows have been moved around,
     * {@link #dataAdded(int, int)} or {@link #dataRemoved(int, int)} should
     * ideally be used instead.
     * 
     * @param firstRowIndex
     *            the index of the first changed row
     * @param numberOfRows
     *            the number of changed rows
     */
    public void dataUpdated(int firstRowIndex, int numberOfRows);

    /**
     * Called when rows have been removed from the data source.
     * 
     * @param firstRowIndex
     *            the index that the first removed row had prior to removal
     * @param numberOfRows
     *            the number of removed rows
     */
    public void dataRemoved(int firstRowIndex, int numberOfRows);

    /**
     * Called when the new rows have been added to the container.
     * 
     * @param firstRowIndex
     *            the index of the first added row
     * @param numberOfRows
     *            the number of added rows
     */
    public void dataAdded(int firstRowIndex, int numberOfRows);
}
