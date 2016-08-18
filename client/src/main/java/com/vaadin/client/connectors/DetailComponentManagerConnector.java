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

import com.vaadin.client.ServerConnector;
import com.vaadin.client.extensions.AbstractExtensionConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.ui.Grid.DetailComponentManager;

/**
 * Client-side connector for the DetailComponentManager of Grid.
 *
 * @since 7.6.1
 */
@Connect(DetailComponentManager.class)
public class DetailComponentManagerConnector
        extends AbstractExtensionConnector {

    @Override
    protected void extend(ServerConnector target) {
        // TODO: Move DetailsGenerator logic here.
    }

}
