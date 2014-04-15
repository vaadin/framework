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

package com.vaadin.data;

import java.io.Serializable;

import com.vaadin.data.Validator.InvalidValueException;

/**
 * <p>
 * Defines the interface to commit and discard changes to an object, supporting
 * buffering.
 * 
 * <p>
 * In <i>buffered</i> mode the initial value is read from the data source and
 * then buffered. Any subsequential writes or reads will be done on the buffered
 * value. Calling {@link #commit()} will write the buffered value to the data
 * source while calling {@link #discard()} while discard the buffered value and
 * re-read the value from the data source.
 * 
 * <p>
 * In <i>non-buffered</i> mode the value is always read directly from the data
 * source. Any write is done directly to the data source with no buffering in
 * between. Reads are also done directly from the data source. Calling
 * {@link #commit()} or {@link #discard()} in this mode is efficiently a no-op.
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
public interface Buffered extends Serializable {

    /**
     * Updates all changes since the previous commit to the data source. The
     * value stored in the object will always be updated into the data source
     * when <code>commit</code> is called.
     * 
     * @throws SourceException
     *             if the operation fails because of an exception is thrown by
     *             the data source. The cause is included in the exception.
     * @throws InvalidValueException
     *             if the operation fails because validation is enabled and the
     *             values do not validate
     */
    public void commit() throws SourceException, InvalidValueException;

    /**
     * Discards all changes since last commit. The object updates its value from
     * the data source.
     * 
     * @throws SourceException
     *             if the operation fails because of an exception is thrown by
     *             the data source. The cause is included in the exception.
     */
    public void discard() throws SourceException;

    /**
     * Sets the buffered mode to the specified status.
     * <p>
     * When in buffered mode, an internal buffer will be used to store changes
     * until {@link #commit()} is called. Calling {@link #discard()} will revert
     * the internal buffer to the value of the data source.
     * <p>
     * When in non-buffered mode both read and write operations will be done
     * directly on the data source. In this mode the {@link #commit()} and
     * {@link #discard()} methods serve no purpose.
     * 
     * @param buffered
     *            true if buffered mode should be turned on, false otherwise
     * @since 7.0
     */
    public void setBuffered(boolean buffered);

    /**
     * Checks the buffered mode
     * 
     * @return true if buffered mode is on, false otherwise
     * @since 7.0
     */
    public boolean isBuffered();

    /**
     * Tests if the value stored in the object has been modified since it was
     * last updated from the data source.
     * 
     * @return <code>true</code> if the value in the object has been modified
     *         since the last data source update, <code>false</code> if not.
     */
    public boolean isModified();

    /**
     * An exception that signals that one or more exceptions occurred while a
     * buffered object tried to access its data source or if there is a problem
     * in processing a data source.
     * 
     * @author Vaadin Ltd.
     * @since 3.0
     */
    @SuppressWarnings("serial")
    public class SourceException extends RuntimeException implements
            Serializable {

        /** Source class implementing the buffered interface */
        private final Buffered source;

        /** Original cause of the source exception */
        private Throwable[] causes = {};

        /**
         * Creates a source exception that does not include a cause.
         * 
         * @param source
         *            the source object implementing the Buffered interface.
         */
        public SourceException(Buffered source) {
            this.source = source;
        }

        /**
         * Creates a source exception from multiple causes.
         * 
         * @param source
         *            the source object implementing the Buffered interface.
         * @param causes
         *            the original causes for this exception.
         */
        public SourceException(Buffered source, Throwable... causes) {
            this.source = source;
            this.causes = causes;
        }

        /**
         * Gets the cause of the exception.
         * 
         * @return The (first) cause for the exception, null if no cause.
         */
        @Override
        public final Throwable getCause() {
            if (causes.length == 0) {
                return null;
            }
            return causes[0];
        }

        /**
         * Gets all the causes for this exception.
         * 
         * @return throwables that caused this exception
         */
        public final Throwable[] getCauses() {
            return causes;
        }

        /**
         * Gets a source of the exception.
         * 
         * @return the Buffered object which generated this exception.
         */
        public Buffered getSource() {
            return source;
        }

    }
}
