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
package com.vaadin.shared.ui.orderedlayout;

import java.io.Serializable;
import java.util.HashMap;

import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.AbstractLayoutState;
import com.vaadin.shared.ui.AlignmentInfo;

public class AbstractOrderedLayoutState extends AbstractLayoutState {
    private boolean spacing = false;

    private HashMap<Connector, ChildComponentData> childData = new HashMap<Connector, ChildComponentData>();

    private int marginsBitmask = 0;

    public static class ChildComponentData implements Serializable {
        private int alignmentBitmask = AlignmentInfo.TOP_LEFT.getBitMask();
        private float expandRatio = 0.0f;

        public int getAlignmentBitmask() {
            return alignmentBitmask;
        }

        public void setAlignmentBitmask(int alignmentBitmask) {
            this.alignmentBitmask = alignmentBitmask;
        }

        public float getExpandRatio() {
            return expandRatio;
        }

        public void setExpandRatio(float expandRatio) {
            this.expandRatio = expandRatio;
        }

    }

    public HashMap<Connector, ChildComponentData> getChildData() {
        return childData;
    }

    public void setChildData(HashMap<Connector, ChildComponentData> childData) {
        this.childData = childData;
    }

    public boolean isSpacing() {
        return spacing;
    }

    public void setSpacing(boolean spacing) {
        this.spacing = spacing;
    }

    public int getMarginsBitmask() {
        return marginsBitmask;
    }

    public void setMarginsBitmask(int marginsBitmask) {
        this.marginsBitmask = marginsBitmask;
    }

}