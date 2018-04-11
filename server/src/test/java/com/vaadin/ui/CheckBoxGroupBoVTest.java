package com.vaadin.ui;

import java.util.EnumSet;

/**
 * Option group test from Book of Vaadin
 *
 * @author Vaadin Ltd
 * @since 8.0
 */
public class CheckBoxGroupBoVTest {
    public enum Status {
        STATE_A, STATE_B, STATE_C, STATE_D;

        public String getCaption() {
            return "** " + toString();
        }
    }

    public void createOptionGroup() {
        CheckBoxGroup<Status> s = new CheckBoxGroup<>();
        s.setItems(EnumSet.allOf(Status.class));
        s.setItemCaptionGenerator(Status::getCaption);
    }

}
