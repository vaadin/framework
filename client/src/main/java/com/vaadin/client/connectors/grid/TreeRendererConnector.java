/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.client.connectors.grid;

import com.google.gwt.safehtml.shared.SafeHtmlUtils;
import com.google.gwt.user.client.Element;
import com.vaadin.client.renderers.HtmlRenderer;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.widget.grid.RendererCellReference;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.tree.TreeRendererState;
import com.vaadin.ui.Tree.TreeRenderer;

import elemental.json.JsonObject;

/**
 * Connector for TreeRenderer
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
@Connect(TreeRenderer.class)
public class TreeRendererConnector
        extends AbstractGridRendererConnector<String> {

    @Override
    public Renderer<String> createRenderer() {
        return new HtmlRenderer() {

            @Override
            public void render(RendererCellReference cell, String htmlString) {
                String content = "<span class=\"v-captiontext\">" +
                        SafeHtmlUtils.htmlEscape(htmlString)
                        + "</span>";

                JsonObject row = getParent().getParent().getDataSource()
                        .getRow(cell.getRowIndex());
                if (row != null && row.hasKey("itemIcon")) {
                    String resourceId = row.getString("itemIcon");
                    Element element = getConnection()
                            .getIcon(getResourceUrl(resourceId)).getElement();
                    content = element.getString() + content;
                }
                super.render(cell, content);
            }
        };
    }

    @Override
    public ColumnConnector getParent() {
        return (ColumnConnector) super.getParent();
    }

    @Override
    public TreeRendererState getState() {
        return (TreeRendererState) super.getState();
    }
}
