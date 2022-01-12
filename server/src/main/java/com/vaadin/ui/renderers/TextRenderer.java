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
package com.vaadin.ui.renderers;

import com.vaadin.ui.Grid.AbstractRenderer;
import elemental.json.JsonValue;

/**
 * A renderer for presenting simple plain-text string values.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
public class TextRenderer extends AbstractRenderer<String> {

    /**
     * Creates a new text renderer
     */
    public TextRenderer() {
        this("");
    }

    /**
     * Creates a new text renderer
     *
     * @param nullRepresentation
     *            the textual representation of {@code null} value
     */
    public TextRenderer(String nullRepresentation) {
        super(String.class, nullRepresentation);
    }

    @Override
    public String getNullRepresentation() {
        return super.getNullRepresentation();
    }
}
