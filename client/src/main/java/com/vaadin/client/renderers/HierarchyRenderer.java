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
package com.vaadin.client.renderers;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.event.dom.client.ClickEvent;
import com.google.gwt.event.dom.client.ClickHandler;
import com.google.gwt.event.dom.client.HasClickHandlers;
import com.google.gwt.event.shared.HandlerRegistration;
import com.google.gwt.user.client.DOM;
import com.google.gwt.user.client.ui.Composite;
import com.google.gwt.user.client.ui.FlowPanel;
import com.google.gwt.user.client.ui.HTML;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.WidgetUtil;
import com.vaadin.client.widget.grid.RendererCellReference;
import com.vaadin.client.widget.treegrid.HierarchyRendererCellReferenceWrapper;
import com.vaadin.shared.ui.treegrid.TreeGridCommunicationConstants;

import elemental.json.JsonObject;

/**
 * A renderer for displaying hierarchical columns in TreeGrid.
 * 
 * @author Vaadin Ltd
 * @since 8.1
 */
public class HierarchyRenderer extends ClickableRenderer<Object, Widget> {
    
    private static final String CLASS_TREE_GRID_NODE = "v-tree-grid-node";
    private static final String CLASS_TREE_GRID_EXPANDER = "v-tree-grid-expander";
    private static final String CLASS_TREE_GRID_CELL_CONTENT = "v-tree-grid-cell-content";
    private static final String CLASS_COLLAPSED = "collapsed";
    private static final String CLASS_EXPANDED = "expanded";
    private static final String CLASS_DEPTH = "depth-";

    private Renderer innerRenderer;

    @Override
    public Widget createWidget() {
        return new HierarchyItem(CLASS_TREE_GRID_NODE);
    }

    @Override
    public void render(RendererCellReference cell, Object data, Widget widget) {

        JsonObject row = (JsonObject) cell.getRow();

        int depth = 0;
        boolean leaf = false;
        boolean collapsed = false;
        if (row.hasKey(
                TreeGridCommunicationConstants.ROW_HIERARCHY_DESCRIPTION)) {
            JsonObject rowDescription = row.getObject(
                    TreeGridCommunicationConstants.ROW_HIERARCHY_DESCRIPTION);

            depth = (int) rowDescription
                    .getNumber(TreeGridCommunicationConstants.ROW_DEPTH);
            leaf = rowDescription
                    .getBoolean(TreeGridCommunicationConstants.ROW_LEAF);
            if (!leaf) {
                collapsed = rowDescription.getBoolean(
                        TreeGridCommunicationConstants.ROW_COLLAPSED);
            }
        }

        HierarchyItem cellWidget = (HierarchyItem) widget;
        cellWidget.setDepth(depth);

        if (leaf) {
            cellWidget.setExpanderState(ExpanderState.LEAF);
        } else if (collapsed) {
            cellWidget.setExpanderState(ExpanderState.COLLAPSED);
        } else {
            cellWidget.setExpanderState(ExpanderState.EXPANDED);
        }

        // Render the contents of the inner renderer. For non widget renderers
        // the cell reference needs to be wrapped so that its getElement method
        // returns the correct element we want to render.
        if (innerRenderer instanceof WidgetRenderer) {
            ((WidgetRenderer) innerRenderer).render(cell, data, ((HierarchyItem) widget).content);
        } else {
            innerRenderer.render(new HierarchyRendererCellReferenceWrapper(cell,
                    ((HierarchyItem) widget).content.getElement()), data);
        }
    }

    /**
     * Sets the renderer to be wrapped. This is the original renderer before hierarchy is applied.
     *
     * @param innerRenderer
     *         Renderer to be wrapped.
     */
    public void setInnerRenderer(Renderer innerRenderer) {
        this.innerRenderer = innerRenderer;
    }

    /**
     * Returns the wrapped renderer.
     *
     * @return Wrapped renderer.
     */
    public Renderer getInnerRenderer() {
        return this.innerRenderer;
    }

    /**
     * Decides whether the element was rendered by {@link HierarchyRenderer}
     */
    public static boolean isElementInHierarchyWidget(Element element) {
        Widget w = WidgetUtil.findWidget(element, null);

        while (w != null) {
            if (w instanceof HierarchyItem) {
                return true;
            }
            w = w.getParent();
        }

        return false;
    }

    private class HierarchyItem extends Composite {

        private FlowPanel panel;
        private Expander expander;
        private Widget content;

        private HierarchyItem(String className) {
            panel = new FlowPanel();
            panel.getElement().addClassName(className);

            expander = new Expander();
            expander.getElement().addClassName(CLASS_TREE_GRID_EXPANDER);

            if (innerRenderer instanceof WidgetRenderer) {
                content = ((WidgetRenderer) innerRenderer).createWidget();
            } else {
                // TODO: 20/09/16 create more general widget?
                content = GWT.create(HTML.class);
            }

            content.getElement().addClassName(CLASS_TREE_GRID_CELL_CONTENT);

            panel.add(expander);
            panel.add(content);

            expander.addClickHandler(HierarchyRenderer.this);

            initWidget(panel);
        }

        private void setDepth(int depth) {
            String classNameToBeReplaced = getFullClassName(CLASS_DEPTH, panel.getElement().getClassName());
            if (classNameToBeReplaced == null) {
                panel.getElement().addClassName(CLASS_DEPTH + depth);
            } else {
                panel.getElement().replaceClassName(classNameToBeReplaced, CLASS_DEPTH + depth);
            }
        }

        private String getFullClassName(String prefix, String classNameList) {
            int start = classNameList.indexOf(prefix);
            int end = start + prefix.length();
            if (start > -1) {
                while (end < classNameList.length() && classNameList.charAt(end) != ' ') {
                    end++;
                }
                return classNameList.substring(start, end);
            }
            return null;
        }

        private void setExpanderState(ExpanderState state) {
            switch (state) {
            case EXPANDED:
                expander.getElement().removeClassName(CLASS_COLLAPSED);
                expander.getElement().addClassName(CLASS_EXPANDED);
                break;
            case COLLAPSED:
                expander.getElement().removeClassName(CLASS_EXPANDED);
                expander.getElement().addClassName(CLASS_COLLAPSED);
                break;
            case LEAF:
            default:
                expander.getElement().removeClassName(CLASS_COLLAPSED);
                expander.getElement().removeClassName(CLASS_EXPANDED);
            }
        }

        private class Expander extends Widget implements HasClickHandlers {

            private Expander() {
                Element span = DOM.createSpan();
                setElement(span);
            }

            @Override
            public HandlerRegistration addClickHandler(ClickHandler handler) {
                return addDomHandler(handler, ClickEvent.getType());
            }
        }
    }

    enum ExpanderState {
        EXPANDED, COLLAPSED, LEAF;
    }
}
