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
package com.vaadin.client.renderers;

import com.google.gwt.safehtml.shared.SafeHtml;
import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.vaadin.client.widget.grid.RendererCellReference;

/**
 * Renders a string as HTML into a cell.
 * <p>
 * The html string is rendered as is without any escaping. It is up to the
 * developer to ensure that the html string honors the {@link SafeHtml}
 * contract. For more information see
 * {@link SafeHtmlUtils#fromSafeConstant(String)}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 * @see SafeHtmlUtils#fromSafeConstant(String)
 */
public class HtmlRenderer implements Renderer<String> {

    @Override
    public void render(RendererCellReference cell, String htmlString) {
        cell.getElement().setInnerSafeHtml(
                SafeHtmlUtils.fromSafeConstant(htmlString));
    }
}
