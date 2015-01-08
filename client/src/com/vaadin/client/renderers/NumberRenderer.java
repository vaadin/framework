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
package com.vaadin.client.renderers;

import com.google.gwt.i18n.client.NumberFormat;
import com.vaadin.client.widget.grid.RendererCellReference;

/**
 * Renders a number into a cell using a specific {@link NumberFormat}. By
 * default uses the default number format returned by
 * {@link NumberFormat#getDecimalFormat()}.
 * 
 * @since 7.4
 * @author Vaadin Ltd
 * @param <T>
 *            The number type to render.
 */
public class NumberRenderer implements Renderer<Number> {

    private NumberFormat format;

    public NumberRenderer() {
        this(NumberFormat.getDecimalFormat());
    }

    public NumberRenderer(NumberFormat format) {
        setFormat(format);
    }

    /**
     * Gets the number format that the number should be formatted in.
     * 
     * @return the number format used to render the number
     */
    public NumberFormat getFormat() {
        return format;
    }

    /**
     * Sets the number format to use for formatting the number.
     * 
     * @param format
     *            the format to use
     * @throws IllegalArgumentException
     *             when the format is null
     */
    public void setFormat(NumberFormat format) throws IllegalArgumentException {
        if (format == null) {
            throw new IllegalArgumentException("Format cannot be null");
        }
        this.format = format;
    }

    @Override
    public void render(RendererCellReference cell, Number number) {
        cell.getElement().setInnerText(format.format(number));
    }
}
