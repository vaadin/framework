/* 
 * Copyright 2011 Vaadin Ltd.
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

package com.vaadin.tests.minitutorials.v7a2;

import com.vaadin.terminal.Resource;
import com.vaadin.terminal.gwt.server.ResourceReference;
import com.vaadin.tests.widgetset.client.minitutorials.v7a2.ResourceInStateState;
import com.vaadin.ui.AbstractComponent;

public class ResourceInStateComponent extends AbstractComponent {
    @Override
    public ResourceInStateState getState() {
        return (ResourceInStateState) super.getState();
    }

    public void setMyIcon(Resource icon) {
        getState().setMyIcon(new ResourceReference(icon));
    }

    public Resource getMyIcon() {
        return ResourceReference.getResource(getState().getMyIcon());
    }
}
