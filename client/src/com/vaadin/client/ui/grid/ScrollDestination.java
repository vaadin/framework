/*
 * Copyright 2000-2013 Vaadin Ltd.
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
package com.vaadin.client.ui.grid;

/**
 * The destinations that are supported in an Escalator when scrolling rows or
 * columns into view.
 * 
 * @since 7.2
 * @author Vaadin Ltd
 */
public enum ScrollDestination {

    /**
     * Scroll as little as possible to show the target element. If the element
     * fits into view, this works as START or END depending on the current
     * scroll position. If the element does not fit into view, this works as
     * START.
     */
    ANY {
        @Override
        double getScrollPos(final double targetStartPx,
                final double targetEndPx, final double viewportStartPx,
                final double viewportEndPx, final int padding) {

            final double startScrollPos = targetStartPx - padding;
            final double viewportLength = viewportEndPx - viewportStartPx;
            final double endScrollPos = targetEndPx + padding - viewportLength;

            if (startScrollPos < viewportStartPx) {
                return startScrollPos;
            } else if (targetEndPx + padding > viewportEndPx) {
                return endScrollPos;
            } else {
                // NOOP, it's already visible
                return viewportStartPx;
            }
        }
    },

    /**
     * Scrolls so that the element is shown at the start of the viewport. The
     * viewport will, however, not scroll beyond its contents.
     */
    START {
        @Override
        double getScrollPos(final double targetStartPx,
                final double targetEndPx, final double viewportStartPx,
                final double viewportEndPx, final int padding) {
            return targetStartPx - padding;
        }
    },

    /**
     * Scrolls so that the element is shown in the middle of the viewport. The
     * viewport will, however, not scroll beyond its contents, given more
     * elements than what the viewport is able to show at once. Under no
     * circumstances will the viewport scroll before its first element.
     */
    MIDDLE {
        @Override
        double getScrollPos(final double targetStartPx,
                final double targetEndPx, final double viewportStartPx,
                final double viewportEndPx, final int padding) {
            final double targetMiddle = targetStartPx
                    + (targetEndPx - targetStartPx) / 2;
            final double viewportLength = viewportEndPx - viewportStartPx;
            return targetMiddle - viewportLength / 2;
        }
    },

    /**
     * Scrolls so that the element is shown at the end of the viewport. The
     * viewport will, however, not scroll before its first element.
     */
    END {
        @Override
        double getScrollPos(final double targetStartPx,
                final double targetEndPx, final double viewportStartPx,
                final double viewportEndPx, final int padding) {
            final double viewportLength = viewportEndPx - viewportStartPx;
            return targetEndPx + padding - viewportLength;
        }
    };

    abstract double getScrollPos(final double targetStartPx,
            final double targetEndPx, final double viewportStartPx,
            final double viewportEndPx, final int padding);
}
