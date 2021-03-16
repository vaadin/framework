/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.data.util.sqlcontainer;

public class TemporaryRowId extends RowId {
    private static final long serialVersionUID = -641983830469018329L;

    public TemporaryRowId(Object... id) {
        super(id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == null || !(TemporaryRowId.class.equals(obj.getClass()))) {
            return false;
        }
        Object[] compId = ((TemporaryRowId) obj).getId();
        return id.equals(compId);
    }

    @Override
    public String toString() {
        return "Temporary row id";
    }

}
