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

import java.util.function.BiConsumer;

import com.google.gwt.core.client.GWT;
import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableCellElement;
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
import com.vaadin.client.ui.treegrid.TreeGridConnector;
import com.vaadin.client.widget.escalator.FlyweightCell;
import com.vaadin.client.widget.grid.RendererCellReference;
import com.vaadin.client.widget.grid.RowReference;
import com.vaadin.shared.data.HierarchicalDataCommunicatorConstants;

import elemental.json.JsonObject;

/**
 * A renderer for displaying hierarchical columns in TreeGrid.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
public class HierarchyRenderer extends ClickableRenderer<Object, Widget> {

    /**
     * Wrapper for cell references. Used to get the correct inner element to
     * render.
     *
     * @author Vaadin Ltd
     * @since 8.1
     */
    private static class HierarchyRendererCellReferenceWrapper
            extends RendererCellReference {

        private Element element;

        public HierarchyRendererCellReferenceWrapper(RendererCellReference cell,
                Element element) {
            super(getRowReference(cell));
            set(getFlyweightCell(cell), cell.getColumnIndex(),
                    cell.getColumn());
            this.element = element;
        }

        @Override
        public TableCellElement getElement() {
            return (TableCellElement) element;
        }

        private native static RowReference<Object> getRowReference(
                RendererCellReference cell) /*-{
            return cell.@com.vaadin.client.widget.grid.CellReference::getRowReference()();
        }-*/;

        private native static FlyweightCell getFlyweightCell(
                RendererCellReference cell) /*-{
            return cell.@com.vaadin.client.widget.grid.RendererCellReference::cell;
        }-*/;
    }

    private String nodeStyleName;
    private String expanderStyleName;
    private String cellContentStyleName;

    private static final String CLASS_COLLAPSED = "collapsed";
    private static final String CLASS_COLLAPSE_DISABLED = "collapse-disabled";
    private static final String CLASS_EXPANDED = "expanded";
    private static final String CLASS_DEPTH = "depth-";

    private Renderer innerRenderer;

    /**
     * Constructs a HierarchyRenderer with given collapse callback. Callback is
     * called when user clicks on the expander of a row. Callback is given the
     * row index and the target collapsed state.
     *
     * @param collapseCallback
     *            the callback for collapsing nodes with row index
     * @param styleName
     *            the style name of the widget this renderer is used in
     */
    public HierarchyRenderer(BiConsumer<Integer, Boolean> collapseCallback,
            String styleName) {
        addClickHandler(event -> {
            try {
                JsonObject row = (JsonObject) event.getRow();
                // Row needs to have hierarchy description
                if (!hasHierarchyData(row)) {
                    return;
                }

                JsonObject hierarchyData = getHierarchyData(row);
                if ((!isCollapsed(hierarchyData)
                        && !TreeGridConnector.isCollapseAllowed(hierarchyData))
                        || isLeaf(hierarchyData)) {
                    return;
                }

                collapseCallback.accept(event.getCell().getRowIndex(),
                        !isCollapsed(hierarchyData));
            } finally {
                event.stopPropagation();
                event.preventDefault();
            }
        });
        setStyleNames(styleName);
    }

    /**
     * Set the style name prefix for the node, expander and cell-content
     * elements.
     *
     * @param styleName
     *            the style name to set
     */
    public void setStyleNames(String styleName) {
        nodeStyleName = styleName + "-node";
        expanderStyleName = styleName + "-expander";
        cellContentStyleName = styleName + "-cell-content";
    }

    @Override
    public Widget createWidget() {
        return new HierarchyItem(nodeStyleName);
    }

    @Override
    public void render(RendererCellReference cell, Object data, Widget widget) {

        JsonObject row = (JsonObject) cell.getRow();

        int depth = 0;
        boolean leaf = false;
        boolean collapsed = false;
        boolean collapseAllowed = true;
        if (hasHierarchyData(row)) {
            JsonObject rowDescription = getHierarchyData(row);

            depth = getDepth(rowDescription);
            leaf = isLeaf(rowDescription);
            if (!leaf) {
                collapsed = isCollapsed(rowDescription);
                collapseAllowed = TreeGridConnector
                        .isCollapseAllowed(rowDescription);
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

        cellWidget.setCollapseAllowed(collapseAllowed);

        // Render the contents of the inner renderer. For non widget
        // renderers
        // the cell reference needs to be wrapped so that its getElement
        // method
        // returns the correct element we want to render.
        if (innerRenderer instanceof WidgetRenderer) {
            ((WidgetRenderer) innerRenderer).render(cell, data,
                    ((HierarchyItem) widget).content);
        } else {
            innerRenderer.render(
                    new HierarchyRendererCellReferenceWrapper(cell,
                            ((HierarchyItem) widget).content.getElement()),
                    data);
        }
    }

    private int getDepth(JsonObject rowDescription) {
        return (int) rowDescription
                .getNumber(HierarchicalDataCommunicatorConstants.ROW_DEPTH);
    }

    private JsonObject getHierarchyData(JsonObject row) {
        return row.getObject(
                HierarchicalDataCommunicatorConstants.ROW_HIERARCHY_DESCRIPTION);
    }

    private boolean hasHierarchyData(JsonObject row) {
        return row.hasKey(
                HierarchicalDataCommunicatorConstants.ROW_HIERARCHY_DESCRIPTION);
    }

    private boolean isLeaf(JsonObject rowDescription) {
        boolean leaf;
        leaf = rowDescription
                .getBoolean(HierarchicalDataCommunicatorConstants.ROW_LEAF);
        return leaf;
    }

    private boolean isCollapsed(JsonObject rowDescription) {
        boolean collapsed;
        collapsed = rowDescription
                .getBoolean(HierarchicalDataCommunicatorConstants.ROW_COLLAPSED);
        return collapsed;
    }

    /**
     * Sets the renderer to be wrapped. This is the original renderer before
     * hierarchy is applied.
     *
     * @param innerRenderer
     *            Renderer to be wrapped.
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
        return innerRenderer;
    }

    /**
     * Decides whether the element was rendered by {@link HierarchyRenderer}
     */
    public static boolean isElementInHierarchyWidget(Element element) {
        Widget w = WidgetUtil.findWidget(element);

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
            expander.getElement().addClassName(expanderStyleName);

            if (innerRenderer instanceof WidgetRenderer) {
                content = ((WidgetRenderer) innerRenderer).createWidget();
            } else {
                // TODO: 20/09/16 create more general widget?
                content = GWT.create(HTML.class);
            }

            content.getElement().addClassName(cellContentStyleName);

            panel.add(expander);
            panel.add(content);

            expander.addClickHandler(HierarchyRenderer.this);

            initWidget(panel);
        }

        private void setDepth(int depth) {
            String classNameToBeReplaced = getFullClassName(CLASS_DEPTH,
                    panel.getElement().getClassName());
            if (classNameToBeReplaced == null) {
                panel.getElement().addClassName(CLASS_DEPTH + depth);
            } else {
                panel.getElement().replaceClassName(classNameToBeReplaced,
                        CLASS_DEPTH + depth);
            }
        }

        private String getFullClassName(String prefix, String classNameList) {
            int start = classNameList.indexOf(prefix);
            int end = start + prefix.length();
            if (start > -1) {
                while (end < classNameList.length()
                        && classNameList.charAt(end) != ' ') {
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

        private void setCollapseAllowed(boolean collapseAllowed) {
            if (expander.getElement().hasClassName(CLASS_EXPANDED)
                    && !collapseAllowed) {
                expander.getElement().addClassName(CLASS_COLLAPSE_DISABLED);
            } else {
                expander.getElement().removeClassName(CLASS_COLLAPSE_DISABLED);
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

    private enum ExpanderState {
        EXPANDED, COLLAPSED, LEAF;
    }
}
