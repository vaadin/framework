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

package com.vaadin.ui;

import org.jsoup.nodes.Element;

import com.vaadin.shared.ui.AbstractLayoutState;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;

/**
 * An abstract class that defines default implementation for the {@link Layout}
 * interface.
 * 
 * @author Vaadin Ltd.
 * @since 5.0
 */
public abstract class AbstractLayout extends AbstractComponentContainer
        implements Layout {

    @Override
    protected AbstractLayoutState getState() {
        return (AbstractLayoutState) super.getState();
    }

    /**
     * Reads margin attributes from a design into a MarginInfo object. This
     * helper method should be called from the
     * {@link #readDesign(Element, DesignContext) readDesign} method of layouts
     * that implement {@link MarginHandler}.
     * 
     * @since 7.5
     * 
     * @param design
     *            the design from which to read
     * @param defMargin
     *            the default margin state for edges that are not set in the
     *            design
     * @param context
     *            the DesignContext instance used for parsing the design
     * @return the margin info
     */
    protected MarginInfo readMargin(Element design, MarginInfo defMargin,
            DesignContext context) {

        if (design.hasAttr("margin")) {
            boolean margin = DesignAttributeHandler.readAttribute("margin",
                    design.attributes(), boolean.class);
            return new MarginInfo(margin);
        } else {
            boolean left = DesignAttributeHandler.readAttribute("margin-left",
                    design.attributes(), defMargin.hasLeft(), boolean.class);

            boolean right = DesignAttributeHandler.readAttribute(
                    "margin-right", design.attributes(), defMargin.hasRight(),
                    boolean.class);

            boolean top = DesignAttributeHandler.readAttribute("margin-top",
                    design.attributes(), defMargin.hasTop(), boolean.class);

            boolean bottom = DesignAttributeHandler.readAttribute(
                    "margin-bottom", design.attributes(),
                    defMargin.hasBottom(), boolean.class);

            return new MarginInfo(top, right, bottom, left);
        }
    }

    /**
     * Writes margin attributes from a MarginInfo object to a design. This
     * helper method should be called from the
     * {@link #readDesign(Element, DesignContext) writeDesign} method of layouts
     * that implement {@link MarginHandler}.
     * 
     * 
     * @since 7.5
     * 
     * @param design
     *            the design to write to
     * @param margin
     *            the margin state to write
     * @param defMargin
     *            the default margin state to compare against
     * @param context
     *            the DesignContext instance used for parsing the design
     */
    protected void writeMargin(Element design, MarginInfo margin,
            MarginInfo defMargin, DesignContext context) {
        if (margin.hasAll()) {
            DesignAttributeHandler.writeAttribute("margin",
                    design.attributes(), margin.hasAll(), defMargin.hasAll(),
                    boolean.class);
        } else {

            DesignAttributeHandler.writeAttribute("margin-left",
                    design.attributes(), margin.hasLeft(), defMargin.hasLeft(),
                    boolean.class);

            DesignAttributeHandler.writeAttribute("margin-right",
                    design.attributes(), margin.hasRight(),
                    defMargin.hasRight(), boolean.class);

            DesignAttributeHandler.writeAttribute("margin-top",
                    design.attributes(), margin.hasTop(), defMargin.hasTop(),
                    boolean.class);

            DesignAttributeHandler.writeAttribute("margin-bottom",
                    design.attributes(), margin.hasBottom(),
                    defMargin.hasBottom(), boolean.class);
        }
    }
}
