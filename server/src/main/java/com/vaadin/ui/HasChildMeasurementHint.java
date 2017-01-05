/*
 * Copyright 2000-2016 Vaadin Ltd.
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
package com.vaadin.ui;

/**
 * Component with layout measuring hint. Used to improve granularity of control
 * over child component measurements.
 *
 * @since 7.6
 * @author Vaadin Ltd
 */
public interface HasChildMeasurementHint extends HasComponents {

    /**
     * Specifies how you would like child components measurements to be handled.
     * Since this is a hint, it can be ignored when deemed necessary.
     */
    public enum ChildMeasurementHint {

        /**
         * Always measure all child components (default).
         */
        MEASURE_ALWAYS,

        /**
         * Measure child component only if child component is a Layout or
         * implements either ManagedLayout or ElementResizeListener.
         */
        MEASURE_IF_NEEDED,

        /**
         * Never measure child components. This can improve rendering speed of
         * components with lots of children (e.g. Table), but can cause some
         * child components to be rendered incorrectly (e.g. ComboBox).
         */
        MEASURE_NEVER

    }

    /**
     * Sets desired child size measurement hint.
     *
     * @param hint
     *            desired hint. A value of null will reset value back to the
     *            default (MEASURE_ALWAYS)
     */
    void setChildMeasurementHint(ChildMeasurementHint hint);

    /**
     * Returns the current child size measurement hint.
     *
     * @return a child measurement hint value
     */
    ChildMeasurementHint getChildMeasurementHint();

}
