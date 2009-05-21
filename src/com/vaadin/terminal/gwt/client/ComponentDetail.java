package com.vaadin.terminal.gwt.client;

import com.vaadin.terminal.gwt.client.RenderInformation.FloatSize;
import com.vaadin.terminal.gwt.client.RenderInformation.Size;

class ComponentDetail {
    private String pid;
    private Paintable component;
    private TooltipInfo tooltipInfo = new TooltipInfo();
    private FloatSize relativeSize;
    private Size offsetSize;

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

}
