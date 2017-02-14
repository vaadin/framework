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
package com.vaadin.client.connectors;

import java.util.ArrayList;
import java.util.Collection;
import java.util.logging.Logger;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.core.client.JsArrayString;
import com.google.gwt.dom.client.NativeEvent;
import com.vaadin.client.BrowserInfo;
import com.vaadin.client.JavaScriptConnectorHelper;
import com.vaadin.client.Util;
import com.vaadin.client.communication.HasJavaScriptConnectorHelper;
import com.vaadin.client.renderers.ComplexRenderer;
import com.vaadin.client.renderers.Renderer;
import com.vaadin.client.widget.grid.CellReference;
import com.vaadin.client.widget.grid.RendererCellReference;
import com.vaadin.shared.JavaScriptExtensionState;
import com.vaadin.shared.ui.Connect;
import com.vaadin.ui.renderers.AbstractJavaScriptRenderer;

import elemental.json.JsonObject;
import elemental.json.JsonValue;

/**
 * Connector for server-side renderer implemented using JavaScript.
 *
 * @since 7.4
 * @author Vaadin Ltd
 */
// This is really typed to <JsonValue>, but because of the way native strings
// are not always instanceof JsonValue, we need to accept Object
@Connect(AbstractJavaScriptRenderer.class)
public class JavaScriptRendererConnector
        extends AbstractRendererConnector<Object>
        implements HasJavaScriptConnectorHelper {
    private final JavaScriptConnectorHelper helper = new JavaScriptConnectorHelper(
            this);

    private final JavaScriptObject cellReferenceWrapper = createCellReferenceWrapper(
            BrowserInfo.get().isIE8());

    @Override
    protected void init() {
        super.init();
        helper.init();

        addGetRowKey(helper.getConnectorWrapper());
    }

    private static native JavaScriptObject createCellReferenceWrapper(
            boolean isIE8)
    /*-{
        var reference = {};
        if (isIE8) {
          // IE8 only supports defineProperty for DOM objects
          reference = $doc.createElement('div');
        }

        var setProperty = function(name, getter, setter) {
            var descriptor = {
                get: getter
            }
            if (setter) {
                descriptor.set = setter;
            }
            Object.defineProperty(reference, name, descriptor);
        };

        setProperty("element", function() {
            return reference.target.@CellReference::getElement()();
        }, null);

        setProperty("rowIndex", function() {
            return reference.target.@CellReference::getRowIndex()();
        }, null);

        setProperty("columnIndex", function() {
            return reference.target.@CellReference::getColumnIndex()();
        }, null);

        setProperty("colSpan", function() {
            return reference.target.@RendererCellReference::getColSpan()();
        }, function(colSpan) {
            reference.target.@RendererCellReference::setColSpan(*)(colSpan);
        });

        return reference;
    }-*/;

    @Override
    public JavaScriptExtensionState getState() {
        return (JavaScriptExtensionState) super.getState();
    }

    private native void addGetRowKey(JavaScriptObject wrapper)
    /*-{
        var self = this;
        wrapper.getRowKey = $entry(function(rowIndex) {
            return @JavaScriptRendererConnector::findRowKey(*)(self, rowIndex);
        });
    }-*/;

    private static String findRowKey(JavaScriptRendererConnector connector,
            int rowIndex) {
        GridConnector gc = (GridConnector) connector.getParent();
        JsonObject row = gc.getWidget().getDataSource().getRow(rowIndex);
        return connector.getRowKey(row);
    }

    private boolean hasFunction(String name) {
        return hasFunction(helper.getConnectorWrapper(), name);
    }

    private static native boolean hasFunction(JavaScriptObject wrapper,
            String name)
    /*-{
        return typeof wrapper[name] === 'function';
    }-*/;

    @Override
    protected Renderer<Object> createRenderer() {
        helper.ensureJavascriptInited();

        if (!hasFunction("render")) {
            throw new RuntimeException(
                    "JavaScriptRenderer " + helper.getInitFunctionName()
                            + " must have a function named 'render'");
        }
        if (hasFunction("destory")) {
            getLogger().severe("Your JavaScript connector ("
                    + helper.getInitFunctionName()
                    + ") has a typo. The destory method should be renamed to destroy.");

        }
        final boolean hasInit = hasFunction("init");
        final boolean hasDestroy = hasFunction("destroy")
                || hasFunction("destory");
        final boolean hasOnActivate = hasFunction("onActivate");
        final boolean hasGetConsumedEvents = hasFunction("getConsumedEvents");
        final boolean hasOnBrowserEvent = hasFunction("onBrowserEvent");

        return new ComplexRenderer<Object>() {
            @Override
            public void render(RendererCellReference cell, Object data) {
                if (data instanceof JsonValue) {
                    data = Util.json2jso((JsonValue) data);
                }
                render(helper.getConnectorWrapper(), getJsCell(cell), data);
            }

            private JavaScriptObject getJsCell(CellReference<?> cell) {
                updateCellReference(cellReferenceWrapper, cell);
                return cellReferenceWrapper;
            }

            public native void render(JavaScriptObject wrapper,
                    JavaScriptObject cell, Object data)
            /*-{
                wrapper.render(cell, data);
            }-*/;

            @Override
            public void init(RendererCellReference cell) {
                if (hasInit) {
                    init(helper.getConnectorWrapper(), getJsCell(cell));
                }
            }

            private native void init(JavaScriptObject wrapper,
                    JavaScriptObject cell)
            /*-{
                wrapper.init(cell);
            }-*/;

            private native void updateCellReference(
                    JavaScriptObject cellWrapper, CellReference<?> target)
            /*-{
                cellWrapper.target = target;
            }-*/;

            @Override
            public void destroy(RendererCellReference cell) {
                if (hasDestroy) {
                    destroy(helper.getConnectorWrapper(), getJsCell(cell));
                } else {
                    super.destroy(cell);
                }
            }

            private native void destroy(JavaScriptObject wrapper,
                    JavaScriptObject cell)
            /*-{
                if (wrapper.destroy) {
                    wrapper.destroy(cell);
                } else  {
                    wrapper.destory(cell);
                }
            }-*/;

            @Override
            public boolean onActivate(CellReference<?> cell) {
                if (hasOnActivate) {
                    return onActivate(helper.getConnectorWrapper(),
                            getJsCell(cell));
                } else {
                    return super.onActivate(cell);
                }
            }

            private native boolean onActivate(JavaScriptObject wrapper,
                    JavaScriptObject cell)
            /*-{
                return !!wrapper.onActivate(cell);
            }-*/;

            @Override
            public Collection<String> getConsumedEvents() {
                if (hasGetConsumedEvents) {
                    JsArrayString events = getConsumedEvents(
                            helper.getConnectorWrapper());

                    ArrayList<String> list = new ArrayList<String>(
                            events.length());
                    for (int i = 0; i < events.length(); i++) {
                        list.add(events.get(i));
                    }
                    return list;
                } else {
                    return super.getConsumedEvents();
                }
            }

            private native JsArrayString getConsumedEvents(
                    JavaScriptObject wrapper)
            /*-{
                var rawEvents = wrapper.getConsumedEvents();
                var events = [];
                for(var i = 0; i < rawEvents.length; i++) {
                  events[i] = ""+rawEvents[i];
                }
                return events;
            }-*/;

            @Override
            public boolean onBrowserEvent(CellReference<?> cell,
                    NativeEvent event) {
                if (hasOnBrowserEvent) {
                    return onBrowserEvent(helper.getConnectorWrapper(),
                            getJsCell(cell), event);
                } else {
                    return super.onBrowserEvent(cell, event);
                }
            }

            private native boolean onBrowserEvent(JavaScriptObject wrapper,
                    JavaScriptObject cell, NativeEvent event)
            /*-{
                return !!wrapper.onBrowserEvent(cell, event);
            }-*/;
        };
    }

    private Logger getLogger() {
        return Logger.getLogger(JavaScriptRendererConnector.class.getName());
    }

    @Override
    public Object decode(JsonValue value) {
        // Let the js logic decode the raw json that the server sent
        return value;
    }

    @Override
    public void onUnregister() {
        super.onUnregister();
        helper.onUnregister();
    }

    @Override
    public JavaScriptConnectorHelper getJavascriptConnectorHelper() {
        return helper;
    }
}
