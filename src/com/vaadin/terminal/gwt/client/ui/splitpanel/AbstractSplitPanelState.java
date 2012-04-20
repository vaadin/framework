/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.terminal.gwt.client.ui.splitpanel;

import java.io.Serializable;

import com.vaadin.terminal.gwt.client.ComponentState;
import com.vaadin.terminal.gwt.client.Connector;

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