/*
 * Copyright 2000-2022 Vaadin Ltd.
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
package com.vaadin.server;

import java.io.Serializable;
import java.util.Objects;

/**
 * Defines the interface to handle exceptions thrown during the execution of a
 * FutureAccess.
 *
 * @since 7.1.8
 * @author Vaadin Ltd
 */
public interface ErrorHandlingRunnable extends Runnable, Serializable {

    /**
     * Handles exceptions thrown during the execution of a FutureAccess.
     * Exceptions thrown by this method are handled by the default error
     * handler.
     *
     * @since 7.1.8
     * @param exception
     *            the thrown exception.
     */
    public void handleError(Exception exception);

    /**
     * Process the given exception in the context of the given runnable. If the
     * runnable extends {@link ErrorHandlingRunnable}, then the exception is
     * passed to {@link #handleError(Exception)} and null is returned. If
     * {@link #handleError(Exception)} throws an exception, that exception is
     * returned. If the runnable does not extend {@link ErrorHandlingRunnable},
     * then the original exception is returned.
     *
     * @since 8.7
     * @param runnable
     *            the runnable for which the exception should be processed, not
     *            <code>null</code>
     * @param exception
     *            the exception to process, not <code>null</code>
     * @return the resulting exception, or <code>null</code> if the exception is
     *         fully processed
     */
    public static Exception processException(Runnable runnable,
            Exception exception) {
        Objects.requireNonNull(runnable, "The runnable cannot be null.");
        if (runnable instanceof ErrorHandlingRunnable) {
            ErrorHandlingRunnable errorHandlingRunnable = (ErrorHandlingRunnable) runnable;

            try {
                errorHandlingRunnable.handleError(exception);
                return null;
            } catch (Exception exceptionFromHandler) {
                return exceptionFromHandler;
            }
        }

        return exception;
    }

}
