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
package com.vaadin.ui.renderers;

import com.vaadin.server.AbstractJavaScriptExtension;
import com.vaadin.server.JavaScriptCallbackHelper;
import com.vaadin.shared.JavaScriptExtensionState;
import com.vaadin.shared.communication.ServerRpc;
import com.vaadin.ui.Grid.AbstractRenderer;
import com.vaadin.ui.JavaScriptFunction;

/**
 * Base class for Renderers with all client-side logic implemented using
 * JavaScript.
 * <p>
 * When a new JavaScript renderer is initialized in the browser, the framework
 * will look for a globally defined JavaScript function that will initialize the
 * renderer. The name of the initialization function is formed by replacing .
 * with _ in the name of the server-side class. If no such function is defined,
 * each super class is used in turn until a match is found. The framework will
 * thus first attempt with <code>com_example_MyRenderer</code> for the
 * server-side
 * <code>com.example.MyRenderer extends AbstractJavaScriptRenderer</code> class.
 * If MyRenderer instead extends <code>com.example.SuperRenderer</code> , then
 * <code>com_example_SuperRenderer</code> will also be attempted if
 * <code>com_example_MyRenderer</code> has not been defined.
 * <p>
 * 
 * In addition to the general JavaScript extension functionality explained in
 * {@link AbstractJavaScriptExtension}, this class also provides some
 * functionality specific for renderers.
 * <p>
 * The initialization function will be called with <code>this</code> pointing to
 * a connector wrapper object providing integration to Vaadin with the following
 * functions:
 * <ul>
 * <li><code>getRowKey(rowIndex)</code> - Gets a unique identifier for the row
 * at the given index. This identifier can be used on the server to retrieve the
 * corresponding ItemId using {@link #getItemId(String)}.</li>
 * </ul>
 * The connector wrapper also supports these special functions that can be
 * implemented by the connector:
 * <ul>
 * <li><code>render(cell, data)</code> - Callback for rendering the given data
 * into the given cell. The structure of cell and data are described in separate
 * sections below. The renderer is required to implement this function.
 * Corresponds to
 * {@link com.vaadin.client.renderers.Renderer#render(com.vaadin.client.widget.grid.RendererCellReference, Object)}
 * .</li>
 * <li><code>init(cell)</code> - Prepares a cell for rendering. Corresponds to
 * {@link com.vaadin.client.renderers.ComplexRenderer#init(com.vaadin.client.widget.grid.RendererCellReference)}
 * .</li>
 * <li><code>destory(cell)</code> - Allows the renderer to release resources
 * allocate for a cell that will no longer be used. Corresponds to
 * {@link com.vaadin.client.renderers.ComplexRenderer#destroy(com.vaadin.client.widget.grid.RendererCellReference)}
 * .</li>
 * <li><code>onActivate(cell)</code> - Called when the cell is activated by the
 * user e.g. by double clicking on the cell or pressing enter with the cell
 * focused. Corresponds to
 * {@link com.vaadin.client.renderers.ComplexRenderer#onActivate(com.vaadin.client.widget.grid.CellReference)}
 * .</li>
 * <li><code>getConsumedEvents()</code> - Returns a JavaScript array of event
 * names that should cause onBrowserEvent to be invoked whenever an event is
 * fired for a cell managed by this renderer. Corresponds to
 * {@link com.vaadin.client.renderers.ComplexRenderer#getConsumedEvents()}.</li>
 * <li><code>onBrowserEvent(cell, event)</code> - Called by Grid when an event
 * of a type returned by getConsumedEvents is fired for a cell managed by this
 * renderer. Corresponds to
 * {@link com.vaadin.client.renderers.ComplexRenderer#onBrowserEvent(com.vaadin.client.widget.grid.CellReference, com.google.gwt.dom.client.NativeEvent)}
 * .</li>
 * </ul>
 * 
 * <p>
 * The cell object passed to functions defined by the renderer has these
 * properties:
 * <ul>
 * <li><code>element</code> - The DOM element corresponding to this cell.
 * Readonly.</li>
 * <li><code>rowIndex</code> - The current index of the row of this cell.
 * Readonly.</li>
 * <li><code>columnIndex</code> - The current index of the column of this cell.
 * Readonly.</li>
 * <li><code>colSpan</code> - The number of columns spanned by this cell. Only
 * supported in the object passed to the <code>render</code> function - other
 * functions should not use the property. Readable and writable.
 * </ul>
 * 
 * @author Vaadin Ltd
 * @since 7.4
 */
public abstract class AbstractJavaScriptRenderer<T> extends AbstractRenderer<T> {
    private JavaScriptCallbackHelper callbackHelper = new JavaScriptCallbackHelper(
            this);

    protected AbstractJavaScriptRenderer(Class<T> presentationType,
            String nullRepresentation) {
        super(presentationType, nullRepresentation);
    }

    protected AbstractJavaScriptRenderer(Class<T> presentationType) {
        super(presentationType, null);
    }

    @Override
    protected <R extends ServerRpc> void registerRpc(R implementation,
            Class<R> rpcInterfaceType) {
        super.registerRpc(implementation, rpcInterfaceType);
        callbackHelper.registerRpc(rpcInterfaceType);
    }

    /**
     * Register a {@link JavaScriptFunction} that can be called from the
     * JavaScript using the provided name. A JavaScript function with the
     * provided name will be added to the connector wrapper object (initially
     * available as <code>this</code>). Calling that JavaScript function will
     * cause the call method in the registered {@link JavaScriptFunction} to be
     * invoked with the same arguments.
     * 
     * @param functionName
     *            the name that should be used for client-side callback
     * @param function
     *            the {@link JavaScriptFunction} object that will be invoked
     *            when the JavaScript function is called
     */
    protected void addFunction(String functionName, JavaScriptFunction function) {
        callbackHelper.registerCallback(functionName, function);
    }

    /**
     * Invoke a named function that the connector JavaScript has added to the
     * JavaScript connector wrapper object. The arguments should only contain
     * data types that can be represented in JavaScript including primitives,
     * their boxed types, arrays, String, List, Set, Map, Connector and
     * JavaBeans.
     * 
     * @param name
     *            the name of the function
     * @param arguments
     *            function arguments
     */
    protected void callFunction(String name, Object... arguments) {
        callbackHelper.invokeCallback(name, arguments);
    }

    @Override
    protected JavaScriptExtensionState getState() {
        return (JavaScriptExtensionState) super.getState();
    }
}
