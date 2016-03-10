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

import java.io.IOException;
import java.io.Serializable;

/**
 * <code>PaintExcepection</code> is thrown if painting of a component fails.
 * 
 * @author Vaadin Ltd.
 * @since 3.0
 */
@SuppressWarnings("serial")
public class PaintException extends IOException implements Serializable {

    /**
     * Constructs an instance of <code>PaintExeception</code> with the specified
     * detail message.
     * 
     * @param msg
     *            the detail message.
     */
    public PaintException(String msg) {
        super(msg);
    }

    /**
     * Constructs an instance of <code>PaintExeception</code> with the specified
     * detail message and cause.
     * 
     * @param msg
     *            the detail message.
     * @param cause
     *            the cause
     */
    public PaintException(String msg, Throwable cause) {
        super(msg, cause);
    }

    /**
     * Constructs an instance of <code>PaintExeception</code> from IOException.
     * 
     * @param exception
     *            the original exception.
     */
    public PaintException(IOException exception) {
        super(exception.getMessage());
    }
}
