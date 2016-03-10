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
