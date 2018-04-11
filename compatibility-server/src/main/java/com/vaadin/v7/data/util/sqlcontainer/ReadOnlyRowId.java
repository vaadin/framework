/*
 * Copyright 2000-2018 Vaadin Ltd.
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
package com.vaadin.v7.data.util.sqlcontainer;

/**
 * @deprecated As of 8.0, no replacement available.
 */
@Deprecated
public class ReadOnlyRowId extends RowId {
    private static final long serialVersionUID = -2626764781642012467L;
    private final Integer rowNum;

    public ReadOnlyRowId(int rowNum) {
        super();
        this.rowNum = rowNum;
    }

    @Override
    public int hashCode() {
        return getRowNum();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(ReadOnlyRowId.class.equals(obj.getClass()))) {
            return false;
        }
        return getRowNum() == (((ReadOnlyRowId) obj).getRowNum());
    }

    public int getRowNum() {
        return rowNum;
    }

    @Override
    public String toString() {
        return String.valueOf(getRowNum());
    }
}
