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
package com.vaadin.data.util.sqlcontainer;

import com.vaadin.data.util.sqlcontainer.query.TableQuery;

/**
 * An OptimisticLockException is thrown when trying to update or delete a row
 * that has been changed since last read from the database.
 * 
 * OptimisticLockException is a runtime exception because optimistic locking is
 * turned off by default, and as such will never be thrown in a default
 * configuration. In order to turn on optimistic locking, you need to specify
 * the version column in your TableQuery instance.
 * 
 * @see TableQuery#setVersionColumn(String)
 * 
 * @author Jonatan Kronqvist / Vaadin Ltd
 */
public class OptimisticLockException extends RuntimeException {

    private final RowId rowId;

    public OptimisticLockException(RowId rowId) {
        super();
        this.rowId = rowId;
    }

    public OptimisticLockException(String msg, RowId rowId) {
        super(msg);
        this.rowId = rowId;
    }

    public RowId getRowId() {
        return rowId;
    }
}
