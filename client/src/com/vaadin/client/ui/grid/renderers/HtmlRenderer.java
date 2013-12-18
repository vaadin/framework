/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.client.ui.grid.renderers;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.vaadin.client.ui.grid.Cell;
import com.vaadin.client.ui.grid.Renderer;

/**
 * Renders a string as HTML into a cell.
 * <p>
 * The html string is HTML-escaped string before rendering. For more information
 * about what kind of escaping is done see
 * {@link SafeHtmlUtils#htmlEscape(String)}.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 * @see SafeHtmlUtils#htmlEscape(String)
 */
public class HtmlRenderer implements Renderer<String> {

    @Override
    public void renderCell(Cell cell, String htmlString) {
        cell.getElement()
                .setInnerSafeHtml(SafeHtmlUtils.fromString(htmlString));
    }
}
