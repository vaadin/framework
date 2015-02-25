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
package com.vaadin.client.ui;

import com.google.gwt.core.client.JavaScriptObject;
import com.google.gwt.dom.client.Element;
import com.google.gwt.user.client.Event;
import com.google.gwt.user.client.ui.Widget;
import com.vaadin.client.widgets.Escalator;

/**
 * A mousewheel handling class to get around the limits of
 * {@link Event#ONMOUSEWHEEL}.
 * 
 * For internal use only. May be removed or replaced in the future.
 * 
 * @see Escalator.JsniWorkaround
 */
abstract class JsniMousewheelHandler {

    /**
     * A JavaScript function that handles the mousewheel DOM event, and passes
     * it on to Java code.
     * 
     * @see #createMousewheelListenerFunction(Widget)
     */
    protected final JavaScriptObject mousewheelListenerFunction;

    protected JsniMousewheelHandler(final Widget widget) {
        mousewheelListenerFunction = createMousewheelListenerFunction(widget);
    }

    /**
     * A method that constructs the JavaScript function that will be stored into
     * {@link #mousewheelListenerFunction}.
     * 
     * @param widget
     *            a reference to the current instance of {@link Widget}
     */
    protected abstract JavaScriptObject createMousewheelListenerFunction(
            Widget widget);

    public native void attachMousewheelListener(Element element)
    /*-{
        if (element.addEventListener) {
            // FireFox likes "wheel", while others use "mousewheel"
            var eventName = 'onmousewheel' in element ? 'mousewheel' : 'wheel';
            element.addEventListener(eventName, this.@com.vaadin.client.ui.JsniMousewheelHandler::mousewheelListenerFunction);
        } else {
            // IE8
            element.attachEvent("onmousewheel", this.@com.vaadin.client.ui.JsniMousewheelHandler::mousewheelListenerFunction);
        }
    }-*/;

    public native void detachMousewheelListener(Element element)
    /*-{
        if (element.addEventListener) {
            // FireFox likes "wheel", while others use "mousewheel"
            var eventName = element.onwheel===undefined?"mousewheel":"wheel";
            element.removeEventListener(eventName, this.@com.vaadin.client.ui.JsniMousewheelHandler::mousewheelListenerFunction);
        } else {
            // IE8
            element.detachEvent("onmousewheel", this.@com.vaadin.client.ui.JsniMousewheelHandler::mousewheelListenerFunction);
        }
    }-*/;

}
