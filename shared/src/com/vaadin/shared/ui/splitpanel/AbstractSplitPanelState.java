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
package com.vaadin.shared.ui.splitpanel;

import java.io.Serializable;

import com.vaadin.shared.AbstractComponentState;
import com.vaadin.shared.Connector;

public class AbstractSplitPanelState extends AbstractComponentState {
    public Connector firstChild = null;
    public Connector secondChild = null;
    public SplitterState splitterState = new SplitterState();

    public static class SplitterState implements Serializable {
        public float position;
        public String positionUnit;
        public float minPosition;
        public String minPositionUnit;
        public float maxPosition;
        public String maxPositionUnit;
        public boolean positionReversed = false;
        public boolean locked = false;
    }
}
