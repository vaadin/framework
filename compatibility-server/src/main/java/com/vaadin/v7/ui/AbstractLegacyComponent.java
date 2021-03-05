/*
 * Copyright 2000-2021 Vaadin Ltd.
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
 * Used since immediate and read-only properties has been removed in Vaadin 8
 * from {@link AbstractComponent}.
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

    /**
     * Tests whether the component is in the read-only mode. The user can not
     * change the value of a read-only component. As only {@code AbstractField}
     * or {@code LegacyField} components normally have a value that can be input
     * or changed by the user, this is mostly relevant only to field components,
     * though not restricted to them.
     *
     * <p>
     * Notice that the read-only mode only affects whether the user can change
     * the <i>value</i> of the component; it is possible to, for example, scroll
     * a read-only table.
     * </p>
     *
     * <p>
     * The method will return {@code true} if the component or any of its
     * parents is in the read-only mode.
     * </p>
     *
     * @return <code>true</code> if the component or any of its parents is in
     *         read-only mode, <code>false</code> if not.
     * @see #setReadOnly(boolean)
     */
    @Override
    public boolean isReadOnly() {
        return getState(false).readOnly;
    }

    /**
     * Sets the read-only mode of the component to the specified mode. The user
     * can not change the value of a read-only component.
     *
     * <p>
     * As only {@code AbstractField} or {@code LegacyField} components normally
     * have a value that can be input or changed by the user, this is mostly
     * relevant only to field components, though not restricted to them.
     * </p>
     *
     * <p>
     * Notice that the read-only mode only affects whether the user can change
     * the <i>value</i> of the component; it is possible to, for example, scroll
     * a read-only table.
     * </p>
     *
     * <p>
     * In Vaadin 8 the read-only property is part of {@link HasValue} API.
     * </p>
     *
     * @param readOnly
     *            a boolean value specifying whether the component is put
     *            read-only mode or not
     */
    @Override
    public void setReadOnly(boolean readOnly) {
        getState().readOnly = readOnly;
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
                    explicitImmediateValue, def.isImmediate(), Boolean.class,
                    designContext);
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
