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

package com.vaadin.server;

import com.vaadin.ui.Component;

/**
 * An extension providing responsive layout capabilities to any Vaadin
 * component. The Responsive extension allows specifying different CSS rules for
 * different dimensions of extended components. This allows creating
 * applications that provide an optimal viewing experience – easy reading and
 * navigation with a minimum of resizing, panning, and scrolling – across a wide
 * range of devices (from mobile phones to desktop computer monitors).
 * <p>
 * NOTE! You should always specify a relative (%) size for the extended
 * component, doing otherwise will prevent the Responsive extension from
 * working, as the component will not dynamically resize.
 * </p>
 * <p>
 * All configuration of the visual breakpoints (ranges) for the component are
 * done with CSS. Pixels (px) are the only supported unit. Fractional pixels are
 * not supported.
 * </p>
 * <p>
 * <i>Dynamic style injections (e.g. through
 * <code>Page.getCurrent().getStyles().add(...)</code>) or any other style
 * updates after the initial page load are not supported at the moment.</i>
 * </p>
 * 
 * 
 * <p>
 * Example:
 * 
 * <b>Java</b>
 * 
 * <pre>
 * CssLayout layout = new CssLayout();
 * layout.setStyleName(&quot;responsive&quot;);
 * layout.setSizeFull();
 * Responsive.makeResponsive(layout);
 * </pre>
 * 
 * <b>SCSS</b>
 * 
 * <pre>
 * .v-csslayout.responsive {
 *   &[width-range~="0-300px"] {
 *     // Styles for the layout when its width is between 0 and 300 pixels
 *   }
 *   &[width-range~="301-500px"] {
 *     // Styles for the layout when its width is between 301 and 500 pixels
 *   }
 *   &[width-range~="501px-"] {
 *     // Styles for the layout when its width is over 500 pixels
 *   }
 *   &[height-range~="0-300px"] {
 *     // Styles for the layout when its height is between 0 and 300 pixels
 *   }
 *   &[height-range~="301-500px"] {
 *     // Styles for the layout when its height is between 301 and 500 pixels
 *   }
 *   &[height-range~="501-"] {
 *     // Styles for the layout when its height is over 500 pixels
 *   }
 * }
 * </pre>
 * 
 * <b>CSS</b>
 * 
 * <pre>
 * .v-csslayout.responsive[width-range~="0-300px"] {
 *    // Styles for the layout when its width is between 0 and 300 pixels
 * }
 * .v-csslayout.responsive[width-range~="301-500px"] {
 *    // Styles for the layout when its width is between 301 and 500 pixels
 * }
 * .v-csslayout.responsive[width-range~="501-"] {
 *    // Styles for the layout when its width is over 500 pixels
 * }
 * 
 * .v-csslayout.responsive[height-range~="0-300px"] {
 *    // Styles for the layout when its height is between 0 and 300 pixels
 * }
 * .v-csslayout.responsive[height-range~="301-500px"] {
 *    // Styles for the layout when its height is between 301 and 500 pixels
 * }
 * .v-csslayout.responsive[height-range~="501px-"] {
 *    // Styles for the layout when its height is over 500 pixels
 * }
 * </pre>
 * 
 * </p>
 * <p>
 * <b>Note:</b> <i>The defined ranges are applied on a global context, so even
 * if you would write your CSS to target only a given context, the ranges would
 * be applied to all other instances with the same style name.</i>
 * </p>
 * <p>
 * E.g. this would affect all CssLayout instances in the application, even
 * though the CSS implies it would only affect CssLayout instances inside a
 * parent with a style name "foobar":
 * </p>
 * 
 * <pre>
 * .foobar .v-csslayout[width-range~="0px-100px"] {
 *    // These properties will affect all responsive CssLayout instances
 * }
 * </pre>
 * 
 * <p>
 * To scope the ranges, use an additional style name for the target component,
 * and add that to your CSS selector:
 * </p>
 * 
 * <pre>
 *  .v-csslayout.mystyle[width-range="0px-100px"] {
 *    // These properties will only affect responsive CssLayout instances with an additional style name of 'mystyle'
 * }
 * </pre>
 * 
 * @author Vaadin Ltd
 * @since 7.2
 */
public class Responsive extends AbstractExtension {

    /**
     * Creates a new instance, which can be used to extend a component.
     */
    protected Responsive() {
    }

    /**
     * Enable responsive width and height range styling for the target component
     * or UI instance.
     * 
     * @param target
     *            The component which should be able to respond to width and/or
     *            height changes.
     */
    public static void makeResponsive(Component... components) {
        for (Component c : components) {
            if (c instanceof AbstractClientConnector) {
                new Responsive().extend((AbstractClientConnector) c);
            }
        }
    }
}
