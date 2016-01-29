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
package com.vaadin.client.data;

/**
 * Empty {@link DataChangeHandler}. Use this when only a few of the methods are
 * needed.
 * 
 * @since
 */
public abstract class AbstractDataChangeHandler implements DataChangeHandler {

    @Override
    public void dataUpdated(int firstRowIndex, int numberOfRows) {
    }

    @Override
    public void dataRemoved(int firstRowIndex, int numberOfRows) {
    }

    @Override
    public void dataAdded(int firstRowIndex, int numberOfRows) {
    }

    @Override
    public void dataAvailable(int firstRowIndex, int numberOfRows) {
    }

    @Override
    public void resetDataAndSize(int estimatedNewDataSize) {
    }
}
