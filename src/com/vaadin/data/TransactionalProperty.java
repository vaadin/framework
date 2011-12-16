package com.vaadin.data;

/**
 * A Property that is capable of handle a transaction that can end in commit or
 * rollback.
 * 
 * @param <T>
 *            The type of the property
 * @author Vaadin Ltd
 * @version @version@
 * @since 7.0
 */
public interface TransactionalProperty<T> extends Property<T> {

    /**
     * Starts a transaction.
     * 
     * <p>
     * If the value is set during a transaction the value must not replace the
     * original value until {@link #commit()} is called. Still,
     * {@link #getValue()} must return the current value set in the transaction.
     * Calling {@link #rollback()} while in a transaction must rollback the
     * value to what it was before the transaction started.
     * </p>
     * <p>
     * {@link ValueChangeEvent}s must not be emitted for internal value changes
     * during a transaction. If the value changes as a result of
     * {@link #commit()}, a {@link ValueChangeEvent} should be emitted.
     * </p>
     */
    public void startTransaction();

    /**
     * Commits and ends the transaction that is in progress.
     * <p>
     * If the value is changed as a result of this operation, a
     * {@link ValueChangeEvent} is emitted if such are supported.
     * <p>
     * This method has no effect if there is no transaction is in progress.
     * <p>
     * This method must never throw an exception.
     */
    public void commit();

    /**
     * Aborts and rolls back the transaction that is in progress.
     * <p>
     * The value is reset to the value before the transaction started. No
     * {@link ValueChangeEvent} is emitted as a result of this.
     * <p>
     * This method has no effect if there is no transaction is in progress.
     * <p>
     * This method must never throw an exception.
     */
    public void rollback();
}
