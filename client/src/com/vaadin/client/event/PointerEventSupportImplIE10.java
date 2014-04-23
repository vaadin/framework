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
package com.vaadin.client.event;

import com.vaadin.client.event.PointerEvent.EventType;

/**
 * Pointer event support class for IE 10 ("ms" prefixed pointer events)
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public class PointerEventSupportImplIE10 extends
        PointerEventSupportImplModernIE {

    @Override
    public String getNativeEventName(EventType eventName) {
        return "MS" + eventName;
    }

}
