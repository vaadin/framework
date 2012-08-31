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

package com.vaadin.server;

import java.io.Serializable;

/**
 * An interface that provides information about the user's terminal.
 * Implementors typically provide additional information using methods not in
 * this interface. </p>
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 * @deprecated Currently only a container for ErrorEvent and ErrorListener
 */
@Deprecated
public interface Terminal extends Serializable {

    /**
     * An error event implementation for Terminal.
     */
    public interface ErrorEvent extends Serializable {

        /**
         * Gets the contained throwable, the cause of the error.
         */
        public Throwable getThrowable();

    }

    /**
     * Interface for listening to Terminal errors.
     */
    public interface ErrorListener extends Serializable {

        /**
         * Invoked when a terminal error occurs.
         * 
         * @param event
         *            the fired event.
         */
        public void terminalError(Terminal.ErrorEvent event);
    }
}
