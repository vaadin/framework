/*
@ITMillApache2LicenseForJavaFiles@
 */
package com.vaadin.data.util.sqlcontainer;

/**
 * An OptimisticLockException is thrown when trying to update or delete a row
 * that has been changed since last read from the database.
 * 
 * OptimisticLockException is a runtime exception because optimistic locking is
 * turned off by default, and as such will never be thrown in a default
 * configuration. In order to turn on optimistic locking, you need to specify
 * the version column in your TableQuery instance.
 * 
 * @see com.vaadin.addon.sqlcontainer.query.TableQuery#setVersionColumn(String)
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
