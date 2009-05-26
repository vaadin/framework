package com.vaadin.terminal.gwt.client;

import java.util.HashMap;

import com.vaadin.terminal.gwt.client.RenderInformation.FloatSize;
import com.vaadin.terminal.gwt.client.RenderInformation.Size;

class ComponentDetail {
    private String pid;
    private Paintable component;
    private TooltipInfo tooltipInfo = new TooltipInfo();

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

    private FloatSize relativeSize;
    private Size offsetSize;
    private HashMap<Object, TooltipInfo> additionalTooltips;

    /**
     * @return the pid
     */
    String getPid() {
        return pid;
    }

    /**
     * @param pid
     *            the pid to set
     */
    void setPid(String pid) {
        this.pid = pid;
    }

    /**
     * @return the component
     */
    Paintable getComponent() {
        return component;
    }

    /**
     * @param component
     *            the component to set
     */
    void setComponent(Paintable component) {
        this.component = component;
    }

    /**
     * @return the relativeSize
     */
    FloatSize getRelativeSize() {
        return relativeSize;
    }

    /**
     * @param relativeSize
     *            the relativeSize to set
     */
    void setRelativeSize(FloatSize relativeSize) {
        this.relativeSize = relativeSize;
    }

    /**
     * @return the offsetSize
     */
    Size getOffsetSize() {
        return offsetSize;
    }

    /**
     * @param offsetSize
     *            the offsetSize to set
     */
    void setOffsetSize(Size offsetSize) {
        this.offsetSize = offsetSize;
    }

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

}
