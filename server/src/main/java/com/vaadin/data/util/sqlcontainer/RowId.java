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

import java.io.Serializable;
import java.util.Arrays;

/**
 * RowId represents identifiers of a single database result set row.
 * 
 * The data structure of a RowId is an Object array which contains the values of
 * the primary key columns of the identified row. This allows easy equals()
 * -comparison of RowItems.
 */
public class RowId implements Serializable {
    private static final long serialVersionUID = -3161778404698901258L;
    protected Object[] id;

    /**
     * Prevent instantiation without required parameters.
     */
    protected RowId() {
    }

    public RowId(Object... id) {
        if (id == null) {
            throw new IllegalArgumentException("id parameter must not be null!");
        }
        this.id = id;
    }

    public Object[] getId() {
        return id;
    }

    @Override
    public int hashCode() {
        return Arrays.hashCode(getId());
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(RowId.class.equals(obj.getClass()))) {
            return false;
        }
        return Arrays.equals(getId(), ((RowId) obj).getId());
    }

    @Override
    public String toString() {
        if (getId() == null) {
            return "";
        }
        StringBuilder builder = new StringBuilder();
        for (Object id : getId()) {
            builder.append(id);
            builder.append('/');
        }
        if (builder.length() > 0) {
            return builder.substring(0, builder.length() - 1);
        }
        return builder.toString();
    }
}
