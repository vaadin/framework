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
package com.vaadin.data.util.sqlcontainer.query;

import java.io.Serializable;

/**
 * OrderBy represents a sorting rule to be applied to a query made by the
 * SQLContainer's QueryDelegate.
 * 
 * The sorting rule is simple and contains only the affected column's name and
 * the direction of the sort.
 */
public class OrderBy implements Serializable {
    private String column;
    private boolean isAscending;

    /**
     * Prevent instantiation without required parameters.
     */
    @SuppressWarnings("unused")
    private OrderBy() {
    }

    public OrderBy(String column, boolean isAscending) {
        setColumn(column);
        setAscending(isAscending);
    }

    public void setColumn(String column) {
        this.column = column;
    }

    public String getColumn() {
        return column;
    }

    public void setAscending(boolean isAscending) {
        this.isAscending = isAscending;
    }

    public boolean isAscending() {
        return isAscending;
    }
}
