package com.vaadin.data.util.filter;

import java.io.Serializable;

/**
 * Exception for cases where a container does not support a specific type of
 * filters.
 * 
 * If possible, this should be thrown already when adding a filter to a
 * container. If a problem is not detected at that point, an
 * {@link UnsupportedOperationException} can be throws when attempting to
 * perform filtering.
 * 
 * @since 6.6
 */
public class UnsupportedFilterException extends RuntimeException implements
        Serializable {
    public UnsupportedFilterException() {
    }

    public UnsupportedFilterException(String message) {
        super(message);
    }

    public UnsupportedFilterException(Exception cause) {
        super(cause);
    }

    public UnsupportedFilterException(String message, Exception cause) {
        super(message, cause);
    }
}