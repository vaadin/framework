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
package com.vaadin.client;

import java.util.HashMap;

class ComponentDetail {

    private TooltipInfo tooltipInfo = new TooltipInfo();

    private ServerConnector connector;

    public ComponentDetail() {

    }

    /**
     * Returns a TooltipInfo assosiated with Component. If element is given,
     * returns an additional TooltipInfo.
     * 
     * @param key
     * @return the tooltipInfo
     */
    public TooltipInfo getTooltipInfo(Object key) {
        if (key == null) {
            return tooltipInfo;
        } else {
            if (additionalTooltips != null) {
                return additionalTooltips.get(key);
            } else {
                return null;
            }
        }
    }

    /**
     * @param tooltipInfo
     *            the tooltipInfo to set
     */
    public void setTooltipInfo(TooltipInfo tooltipInfo) {
        this.tooltipInfo = tooltipInfo;
    }

    private HashMap<Object, TooltipInfo> additionalTooltips;

    public void putAdditionalTooltip(Object key, TooltipInfo tooltip) {
        if (tooltip == null && additionalTooltips != null) {
            additionalTooltips.remove(key);
        } else {
            if (additionalTooltips == null) {
                additionalTooltips = new HashMap<Object, TooltipInfo>();
            }
            additionalTooltips.put(key, tooltip);
        }
    }

    public ServerConnector getConnector() {
        return connector;
    }

    public void setConnector(ServerConnector connector) {
        this.connector = connector;
    }

}
