/*
 * Copyright 2000-2021 Vaadin Ltd.
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
package com.vaadin.shared.ui;

/**
 * Represents the error levels displayed on components.
 *
 * @author Vaadin Ltd
 * @since 8.2
 */
public enum ErrorLevel {

    /**
     * Error level for informational messages.
     */
    INFO,

    /**
     * Error level for warning messages.
     */
    WARNING,

    /**
     * Error level for regular messages.
     */
    ERROR,

    /**
     * Error level for critical messages.
     */
    CRITICAL,

    /**
     * Error level for system errors and bugs.
     */
    SYSTEM;

    /**
     * Integer representation of error severity for comparison.
     *
     * @return integer for error severity
     */
    public int intValue() {
        return ordinal();
    }
}
