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

import java.util.Collection;
import java.util.Iterator;

/**
 * Class for combining multiple error messages together.
 * 
 * @author Vaadin Ltd
 * @since 3.0
 */
@SuppressWarnings("serial")
public class CompositeErrorMessage extends AbstractErrorMessage {

    /**
     * Constructor for CompositeErrorMessage.
     * 
     * @param errorMessages
     *            the array of error messages that are listed together. Nulls
     *            are ignored, but at least one message is required.
     */
    public CompositeErrorMessage(ErrorMessage... errorMessages) {
        super(null);
        setErrorLevel(ErrorLevel.INFORMATION);

        for (int i = 0; i < errorMessages.length; i++) {
            addErrorMessage(errorMessages[i]);
        }

        if (getCauses().size() == 0) {
            throw new IllegalArgumentException(
                    "Composite error message must have at least one error");
        }

    }

    /**
     * Constructor for CompositeErrorMessage.
     * 
     * @param errorMessages
     *            the Collection of error messages that are listed together. At
     *            least one message is required.
     */
    public CompositeErrorMessage(
            Collection<? extends ErrorMessage> errorMessages) {
        super(null);
        setErrorLevel(ErrorLevel.INFORMATION);

        for (final Iterator<? extends ErrorMessage> i = errorMessages
                .iterator(); i.hasNext();) {
            addErrorMessage(i.next());
        }

        if (getCauses().size() == 0) {
            throw new IllegalArgumentException(
                    "Composite error message must have at least one error");
        }
    }

    /**
     * Adds a error message into this composite message. Updates the level
     * field.
     * 
     * @param error
     *            the error message to be added. Duplicate errors are ignored.
     */
    private void addErrorMessage(ErrorMessage error) {
        if (error != null && !getCauses().contains(error)) {
            addCause(error);
            if (error.getErrorLevel().intValue() > getErrorLevel().intValue()) {
                setErrorLevel(error.getErrorLevel());
            }
        }
    }

    /**
     * Gets Error Iterator.
     * 
     * @return the error iterator.
     */
    public Iterator<ErrorMessage> iterator() {
        return getCauses().iterator();
    }

    /**
     * Returns a comma separated list of the error messages.
     * 
     * @return String, comma separated list of error messages.
     */
    @Override
    public String toString() {
        String retval = "[";
        int pos = 0;
        for (final Iterator<ErrorMessage> i = getCauses().iterator(); i
                .hasNext();) {
            if (pos > 0) {
                retval += ",";
            }
            pos++;
            retval += i.next().toString();
        }
        retval += "]";

        return retval;
    }
}
