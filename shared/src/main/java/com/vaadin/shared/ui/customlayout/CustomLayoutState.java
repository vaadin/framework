/*
 * Copyright 2000-2021 Vaadin Ltd.
 *
 * Licensed under the Commercial Vaadin Developer License version 4.0 (CVDLv4); 
 * you may not use this file except in compliance with the License. You may obtain
 * a copy of the License at
 *
 * https://vaadin.com/license/cvdl-4.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */
package com.vaadin.shared.ui.customlayout;

import java.util.HashMap;
import java.util.Map;

import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.AbstractLayoutState;

public class CustomLayoutState extends AbstractLayoutState {
    {
        primaryStyleName = "v-customlayout";
    }
    public Map<Connector, String> childLocations = new HashMap<Connector, String>();
    public String templateContents;
    public String templateName;
}
