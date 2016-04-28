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
package com.vaadin.event;

import com.vaadin.event.Action.Listener;
import com.vaadin.server.Resource;

public abstract class ShortcutListener extends ShortcutAction implements
        Listener {

    private static final long serialVersionUID = 1L;

    public ShortcutListener(String caption, int keyCode, int... modifierKeys) {
        super(caption, keyCode, modifierKeys);
    }

    public ShortcutListener(String shorthandCaption, int... modifierKeys) {
        super(shorthandCaption, modifierKeys);
    }

    public ShortcutListener(String caption, Resource icon, int keyCode,
            int... modifierKeys) {
        super(caption, icon, keyCode, modifierKeys);
    }

    public ShortcutListener(String shorthandCaption) {
        super(shorthandCaption);
    }

    @Override
    abstract public void handleAction(Object sender, Object target);
}
