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
 * Defines how the client should interpret textual values.
 *
 * @since 8.0
 */
public enum ContentMode {
    /**
     * Textual values are displayed as plain text.
     */
    TEXT,

    /**
     * Textual values are displayed as preformatted text. In this mode newlines
     * are preserved when rendered on the screen.
     */
    PREFORMATTED,

    /**
     * Textual values are interpreted and displayed as HTML. Care should be
     * taken when using this mode to avoid Cross-site Scripting (XSS) issues.
     */
    HTML

}
