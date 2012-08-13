/* 
@VaadinApache2LicenseForJavaFiles@
 */
package com.vaadin.shared.ui.orderedlayout;

import java.io.Serializable;
import java.util.HashMap;

import com.vaadin.shared.Connector;
import com.vaadin.shared.ui.AbstractLayoutState;
import com.vaadin.shared.ui.AlignmentInfo;

public class AbstractOrderedLayoutState extends AbstractLayoutState {
    private boolean spacing = false;

    public HashMap<Connector, ChildComponentData> childData = new HashMap<Connector, ChildComponentData>();

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

}