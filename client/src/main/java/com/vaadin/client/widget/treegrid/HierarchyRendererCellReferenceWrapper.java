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
package com.vaadin.client.widget.treegrid;

import com.google.gwt.dom.client.Element;
import com.google.gwt.dom.client.TableCellElement;
import com.vaadin.client.widget.escalator.FlyweightCell;
import com.vaadin.client.widget.grid.RendererCellReference;
import com.vaadin.client.widget.grid.RowReference;

/**
 * Wrapper for cell references. Used by HierarchyRenderer to get the correct
 * inner element to render.
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
public class HierarchyRendererCellReferenceWrapper
        extends RendererCellReference {

    private Element element;

    public HierarchyRendererCellReferenceWrapper(RendererCellReference cell,
            Element element) {
        super(getRowReference(cell));
        set(getFlyweightCell(cell), cell.getColumnIndex(), cell.getColumn());
        this.element = element;
    }

    @Override
    public TableCellElement getElement() {
        return (TableCellElement) element;
    }

    private native static RowReference<Object> getRowReference(
            RendererCellReference cell)
    /*-{
    return cell.@com.vaadin.client.widget.grid.CellReference::getRowReference()();
    }-*/;

    private native static FlyweightCell getFlyweightCell(
            RendererCellReference cell)
    /*-{
    return cell.@com.vaadin.client.widget.grid.RendererCellReference::cell;
    }-*/;
}
