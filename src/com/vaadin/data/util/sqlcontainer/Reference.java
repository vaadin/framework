/*
@ITMillApache2LicenseForJavaFiles@
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
