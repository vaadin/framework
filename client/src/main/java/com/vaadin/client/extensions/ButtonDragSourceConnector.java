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

import com.vaadin.client.ServerConnector;
import com.vaadin.client.ui.VButton;
import com.vaadin.client.ui.button.ButtonConnector;
import com.vaadin.event.dnd.ButtonDragSource;
import com.vaadin.shared.ui.Connect;

/**
 * Extension to make Button a drag source for HTML5 drag and drop functionality.
 *
 * @author Vaadin Ltd.
 * @since 8.1
 */
@Connect(ButtonDragSource.class)
public class ButtonDragSourceConnector extends DragSourceExtensionConnector {

    private VButton buttonWidget;

    @Override
    protected void extend(ServerConnector target) {
        super.extend(target);

        buttonWidget = ((ButtonConnector) target).getWidget();
        buttonWidget.setCapturingEnabled(false);
    }

    @Override
    public void onUnregister() {
        super.onUnregister();

        buttonWidget.setCapturingEnabled(true);
    }
}
