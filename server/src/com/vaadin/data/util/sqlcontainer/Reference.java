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
 * The reference class represents a simple [usually foreign key] reference to
 * another SQLContainer. Actual foreign key reference in the database is not
 * required, but it is recommended to make sure that certain constraints are
 * followed.
 */
@SuppressWarnings("serial")
class Reference implements Serializable {

    /**
     * The SQLContainer that this reference points to.
     */
    private SQLContainer referencedContainer;

    /**
     * The column ID/name in the referencing SQLContainer that contains the key
     * used for the reference.
     */
    private String referencingColumn;

    /**
     * The column ID/name in the referenced SQLContainer that contains the key
     * used for the reference.
     */
    private String referencedColumn;

    /**
     * Constructs a new reference to be used within the SQLContainer to
     * reference another SQLContainer.
     */
    Reference(SQLContainer referencedContainer, String referencingColumn,
            String referencedColumn) {
        this.referencedContainer = referencedContainer;
        this.referencingColumn = referencingColumn;
        this.referencedColumn = referencedColumn;
    }

    SQLContainer getReferencedContainer() {
        return referencedContainer;
    }

    String getReferencingColumn() {
        return referencingColumn;
    }

    String getReferencedColumn() {
        return referencedColumn;
    }
}
