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
package com.vaadin.server;

@SuppressWarnings("serial")
public class SystemMessageException extends RuntimeException {

    /**
     * Cause of the method exception
     */
    private Throwable cause;

    /**
     * Constructs a new <code>SystemMessageException</code> with the specified
     * detail message.
     * 
     * @param msg
     *            the detail message.
     */
    public SystemMessageException(String msg) {
        super(msg);
    }

    /**
     * Constructs a new <code>SystemMessageException</code> with the specified
     * detail message and cause.
     * 
     * @param msg
     *            the detail message.
     * @param cause
     *            the cause of the exception.
     */
    public SystemMessageException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Constructs a new <code>SystemMessageException</code> from another
     * exception.
     * 
     * @param cause
     *            the cause of the exception.
     */
    public SystemMessageException(Throwable cause) {
        this.cause = cause;
    }

    /**
     * @see java.lang.Throwable#getCause()
     */
    @Override
    public Throwable getCause() {
        return cause;
    }

}
