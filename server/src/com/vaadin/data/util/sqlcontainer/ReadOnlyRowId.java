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

public class ReadOnlyRowId extends RowId {
    private static final long serialVersionUID = -2626764781642012467L;
    private final Integer rowNum;

    public ReadOnlyRowId(int rowNum) {
        super();
        this.rowNum = rowNum;
    }

    @Override
    public int hashCode() {
        return rowNum.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(obj instanceof ReadOnlyRowId)) {
            return false;
        }
        return rowNum.equals(((ReadOnlyRowId) obj).rowNum);
    }

    public int getRowNum() {
        return rowNum;
    }
}
