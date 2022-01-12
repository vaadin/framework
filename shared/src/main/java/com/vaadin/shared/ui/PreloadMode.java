/*
 * Copyright 2000-2022 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4);
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.shared.ui;

/**
 * Enumeration that provides a hint to the browser how media should be
 * preloaded.
 *
 * @since 7.7.11
 */
public enum PreloadMode {
    /**
     * Indicates that the whole video/audio file could be downloaded, even if
     * the user is not expected to use it. This is the default value.
     */
    AUTO,

    /**
     * Indicates that only media metadata (e.g. length) should be preloaded.
     */
    METADATA,

    /**
     * Indicates that the video/audio should not be preloaded.
     */
    NONE;

    /**
     * Returns the preload mode string used by the browser.
     * 
     * @return corresponding preload attribute value string
     */
    public String getValue() {
        return name().toLowerCase();
    }
}
