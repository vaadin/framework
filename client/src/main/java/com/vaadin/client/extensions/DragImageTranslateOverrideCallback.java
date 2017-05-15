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
package com.vaadin.client.extensions;

import elemental.events.TouchEvent;
import elemental.html.HtmlElement;
import elemental.html.Point;
import jsinterop.annotations.JsFunction;

/**
 * Listener used to be able to override drag image location when mobile drag and
 * drop support has been enabled using a polyfill
 * (https://github.com/timruffles/ios-html5-drag-drop-shim/tree/rewrite).
 *
 * @author Vaadin Ltd
 * @since 8.1
 */
@FunctionalInterface
@JsFunction
public interface DragImageTranslateOverrideCallback {
    /**
     * Hook for custom logic that can manipulate the drag image translate
     * offset.
     *
     * @param event
     *            corresponding touchmove event
     * @param hoverCoordinates
     *            the processed touch event viewport coordinates
     * @param hoveredElement
     *            the element under the calculated touch coordinates
     * @param callback
     *            callback for updating the drag image offset
     */
    void execute(TouchEvent event, Point hoverCoordinates,
            HtmlElement hoveredElement, TranslateDragImageCallback callback);
}
