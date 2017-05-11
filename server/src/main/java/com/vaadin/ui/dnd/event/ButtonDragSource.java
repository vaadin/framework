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
package com.vaadin.ui.dnd.event;

import com.vaadin.annotations.Widgetset;
import com.vaadin.shared.ui.dnd.ButtonDragSourceState;
import com.vaadin.ui.Button;
import com.vaadin.ui.dnd.DragSourceExtension;

/**
 * Extension to make Button a drag source for HTML5 drag and drop functionality.
 *
 * @author Vaadin Ltd.
 * @since 8.1
 */
@Widgetset("com.vaadin.DefaultWidgetSet")
public class ButtonDragSource extends DragSourceExtension<Button> {

    public ButtonDragSource(Button target) {
        super(target);
    }

    @Override
    protected ButtonDragSourceState getState() {
        return (ButtonDragSourceState) super.getState();
    }

    @Override
    protected ButtonDragSourceState getState(boolean markAsDirty) {
        return (ButtonDragSourceState) super.getState(markAsDirty);
    }
}
