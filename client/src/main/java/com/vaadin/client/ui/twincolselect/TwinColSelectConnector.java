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

package com.vaadin.client.ui.twincolselect;

import com.vaadin.client.ApplicationConnection;
import com.vaadin.client.DirectionalManagedLayout;
import com.vaadin.client.UIDL;
import com.vaadin.client.ui.VTwinColSelect;
import com.vaadin.client.ui.optiongroup.OptionGroupBaseConnector;
import com.vaadin.shared.ui.Connect;
import com.vaadin.shared.ui.twincolselect.TwinColSelectState;
import com.vaadin.ui.TwinColSelect;

@Connect(TwinColSelect.class)
public class TwinColSelectConnector extends OptionGroupBaseConnector implements
        DirectionalManagedLayout {

    @Override
    public void updateFromUIDL(UIDL uidl, ApplicationConnection client) {
        // Captions are updated before super call to ensure the widths are set
        // correctly
        if (isRealUpdate(uidl)) {
            getWidget().updateCaptions(uidl);
            getLayoutManager().setNeedsHorizontalLayout(this);
        }

        super.updateFromUIDL(uidl, client);
    }

    @Override
    protected void init() {
        super.init();
        getLayoutManager().registerDependency(this,
                getWidget().captionWrapper.getElement());
    }

    @Override
    public void onUnregister() {
        getLayoutManager().unregisterDependency(this,
                getWidget().captionWrapper.getElement());
    }

    @Override
    public VTwinColSelect getWidget() {
        return (VTwinColSelect) super.getWidget();
    }

    @Override
    public TwinColSelectState getState() {
        return (TwinColSelectState) super.getState();
    }

    @Override
    public void layoutVertically() {
        if (isUndefinedHeight()) {
            getWidget().clearInternalHeights();
        } else {
            getWidget().setInternalHeights();
        }
    }

    @Override
    public void layoutHorizontally() {
        if (isUndefinedWidth()) {
            getWidget().clearInternalWidths();
        } else {
            getWidget().setInternalWidths();
        }
    }
}
