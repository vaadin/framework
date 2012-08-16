/*
 * Copyright 2011 Vaadin Ltd.
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

    public RowId(Object[] id) {
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
        int result = 31;
        if (id != null) {
            for (Object o : id) {
                if (o != null) {
                    result += o.hashCode();
                }
            }
        }
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof RowId)) {
            return false;
        }
        Object[] compId = ((RowId) obj).getId();
        if (id == null && compId == null) {
            return true;
        }
        if (id.length != compId.length) {
            return false;
        }
        for (int i = 0; i < id.length; i++) {
            if ((id[i] == null && compId[i] != null)
                    || (id[i] != null && !id[i].equals(compId[i]))) {
                return false;
            }
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuffer s = new StringBuffer();
        for (int i = 0; i < id.length; i++) {
            s.append(id[i]);
            if (i < id.length - 1) {
                s.append("/");
            }
        }
        return s.toString();
    }
}
