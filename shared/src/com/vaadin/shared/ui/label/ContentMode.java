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
package com.vaadin.shared.ui.label;

/**
 * Content modes defining how the client should interpret a Label's value.
 * 
 * @since 7.0.0
 */
public enum ContentMode {
    /**
     * Content mode, where the label contains only plain text.
     */
    TEXT,

    /**
     * Content mode, where the label contains preformatted text. In this mode
     * newlines are preserved when rendered on the screen.
     */
    PREFORMATTED,

    /**
     * Content mode, where the label contains HTML.
     */
    HTML,

    /**
     * Content mode, where the label contains well-formed or well-balanced XML.
     * This is handled in the same way as {@link #HTML}.
     * 
     * @deprecated Use {@link #HTML} instead
     */
    @Deprecated
    XML,

    /**
     * Legacy content mode, where the label contains RAW output. This is handled
     * in exactly the same way as {@link #HTML}.
     * 
     * @deprecated Use {@link #HTML} instead
     */
    @Deprecated
    RAW;
}
