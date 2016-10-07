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
package com.vaadin.v7.ui;

import org.jsoup.nodes.Attributes;
import org.jsoup.nodes.Element;

import com.vaadin.ui.AbstractComponent;
import com.vaadin.ui.declarative.DesignAttributeHandler;
import com.vaadin.ui.declarative.DesignContext;
import com.vaadin.v7.shared.AbstractLegacyComponentState;

/**
 * An abstract base class for compatibility components.
 * <p>
 * Used since immediate property has been removed in Vaadin 8 from
 * {@link AbstractComponent}.
 *
 * @author Vaadin Ltd
 * @since 8.0
 * @deprecated only used for Vaadin 7 compatiblity components
 */
@Deprecated
public class AbstractLegacyComponent extends AbstractComponent {

    private Boolean explicitImmediateValue;

    /**
     * Returns the explicitly set immediate value.
     *
     * @return the explicitly set immediate value or null if
     *         {@link #setImmediate(boolean)} has not been explicitly invoked
     */
    protected Boolean getExplicitImmediateValue() {
        return explicitImmediateValue;
    }

    /**
     * Returns the immediate mode of the component.
     * <p>
     * Since Vaadin 8, the default mode is immediate.
     *
     * @return true if the component is in immediate mode (explicitly or
     *         implicitly set), false if the component if not in immediate mode
     */
    public boolean isImmediate() {
        if (explicitImmediateValue != null) {
            return explicitImmediateValue;
        } else {
            return true;
        }
    }

    /**
     * Sets the component's immediate mode to the specified status.
     * <p>
     * Since Vaadin 8, the default mode is immediate.
     *
     * @param immediate
     *            the boolean value specifying if the component should be in the
     *            immediate mode after the call.
     */
    public void setImmediate(boolean immediate) {
        explicitImmediateValue = immediate;
        getState().immediate = immediate;
    }

    @Override
    public void readDesign(Element design, DesignContext designContext) {
        super.readDesign(design, designContext);

        Attributes attr = design.attributes();
        // handle immediate
        if (attr.hasKey("immediate")) {
            setImmediate(DesignAttributeHandler.getFormatter()
                    .parse(attr.get("immediate"), Boolean.class));
        }
    }

    @Override
    public void writeDesign(Element design, DesignContext designContext) {
        super.writeDesign(design, designContext);

        AbstractLegacyComponent def = designContext.getDefaultInstance(this);
        Attributes attr = design.attributes();
        // handle immediate
        if (explicitImmediateValue != null) {
            DesignAttributeHandler.writeAttribute("immediate", attr,
                    explicitImmediateValue, def.isImmediate(), Boolean.class);
        }
    }

    @Override
    public void beforeClientResponse(boolean initial) {
        super.beforeClientResponse(initial);
        getState().immediate = isImmediate();
    }

    @Override
    protected AbstractLegacyComponentState getState() {
        return (AbstractLegacyComponentState) super.getState();
    }

    @Override
    protected AbstractLegacyComponentState getState(boolean markAsDirty) {
        return (AbstractLegacyComponentState) super.getState(markAsDirty);
    }
}
