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
package com.vaadin.shared.ui.splitpanel;

import java.io.Serializable;

import com.vaadin.shared.ComponentState;
import com.vaadin.shared.Connector;

public class AbstractSplitPanelState extends ComponentState {

    private Connector firstChild = null;
    private Connector secondChild = null;
    private SplitterState splitterState = new SplitterState();

    public boolean hasFirstChild() {
        return firstChild != null;
    }

    public boolean hasSecondChild() {
        return secondChild != null;
    }

    public Connector getFirstChild() {
        return firstChild;
    }

    public void setFirstChild(Connector firstChild) {
        this.firstChild = firstChild;
    }

    public Connector getSecondChild() {
        return secondChild;
    }

    public void setSecondChild(Connector secondChild) {
        this.secondChild = secondChild;
    }

    public SplitterState getSplitterState() {
        return splitterState;
    }

    public void setSplitterState(SplitterState splitterState) {
        this.splitterState = splitterState;
    }

    public static class SplitterState implements Serializable {
        private float position;
        private String positionUnit;
        private float minPosition;
        private String minPositionUnit;
        private float maxPosition;
        private String maxPositionUnit;
        private boolean positionReversed = false;
        private boolean locked = false;

        public float getPosition() {
            return position;
        }

        public void setPosition(float position) {
            this.position = position;
        }

        public String getPositionUnit() {
            return positionUnit;
        }

        public void setPositionUnit(String positionUnit) {
            this.positionUnit = positionUnit;
        }

        public float getMinPosition() {
            return minPosition;
        }

        public void setMinPosition(float minPosition) {
            this.minPosition = minPosition;
        }

        public String getMinPositionUnit() {
            return minPositionUnit;
        }

        public void setMinPositionUnit(String minPositionUnit) {
            this.minPositionUnit = minPositionUnit;
        }

        public float getMaxPosition() {
            return maxPosition;
        }

        public void setMaxPosition(float maxPosition) {
            this.maxPosition = maxPosition;
        }

        public String getMaxPositionUnit() {
            return maxPositionUnit;
        }

        public void setMaxPositionUnit(String maxPositionUnit) {
            this.maxPositionUnit = maxPositionUnit;
        }

        public boolean isPositionReversed() {
            return positionReversed;
        }

        public void setPositionReversed(boolean positionReversed) {
            this.positionReversed = positionReversed;
        }

        public boolean isLocked() {
            return locked;
        }

        public void setLocked(boolean locked) {
            this.locked = locked;
        }

    }
}