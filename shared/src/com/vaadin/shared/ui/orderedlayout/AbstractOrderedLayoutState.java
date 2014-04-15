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
package com.vaadin.shared.ui.orderedlayout;

import java.io.Serializable;
import java.util.HashMap;

import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.AbstractLayoutState;
import com.vaadin.shared.ui.AlignmentInfo;

public class AbstractOrderedLayoutState extends AbstractLayoutState {
    public boolean spacing = false;

    public HashMap<Connector, ChildComponentData> childData = new HashMap<Connector, ChildComponentData>();

    public int marginsBitmask = 0;

    public static class ChildComponentData implements Serializable {

        public int alignmentBitmask = AlignmentInfo.TOP_LEFT.getBitMask();

        public float expandRatio = 0.0f;
    }
}
